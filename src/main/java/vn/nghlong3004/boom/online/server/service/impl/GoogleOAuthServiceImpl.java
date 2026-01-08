package vn.nghlong3004.boom.online.server.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import vn.nghlong3004.boom.online.server.configuration.GoogleOAuthConfiguration;
import vn.nghlong3004.boom.online.server.constant.LocaleConstant;
import vn.nghlong3004.boom.online.server.email.LocaleStrategy;
import vn.nghlong3004.boom.online.server.email.TemplateType;
import vn.nghlong3004.boom.online.server.exception.ErrorCode;
import vn.nghlong3004.boom.online.server.exception.ResourceException;
import vn.nghlong3004.boom.online.server.model.*;
import vn.nghlong3004.boom.online.server.model.response.GoogleOAuthInitResponse;
import vn.nghlong3004.boom.online.server.model.response.GoogleOAuthStatusResponse;
import vn.nghlong3004.boom.online.server.model.response.GoogleTokenResponse;
import vn.nghlong3004.boom.online.server.model.response.GoogleUserInfoResponse;
import vn.nghlong3004.boom.online.server.repository.OAuthSessionRepository;
import vn.nghlong3004.boom.online.server.repository.UserRepository;
import vn.nghlong3004.boom.online.server.service.GoogleOAuthService;
import vn.nghlong3004.boom.online.server.service.TokenService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

  private final Map<String, LocaleStrategy> localeStrategies;
  private final GoogleOAuthConfiguration configuration;
  private final OAuthSessionRepository sessionRepository;
  private final TokenService tokenService;
  private final UserRepository userRepository;
  private final RestTemplate restTemplate;

  @Override
  public GoogleOAuthInitResponse initializeAuth() {
    OAuthSession session = sessionRepository.create();

    String authUrl = buildAuthUrl(session.getState());

    log.info("Initialized OAuth session: {}", session.getSessionId());
    return new GoogleOAuthInitResponse(session.getSessionId(), authUrl);
  }

  @Override
  public String handleCallback(String code, String state) {
    OAuthSession session = sessionRepository.findByState(state);
    if (session == null) {
      throw new IllegalArgumentException("Invalid or expired OAuth state");
    }

    GoogleTokenResponse tokens = exchangeCodeForTokens(code);

    GoogleUserInfoResponse userInfo = fetchUserInfo(tokens.accessToken());

    User user = findOrCreateUser(userInfo);

    var authorities = List.of(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    AuthenticatedUser authenticatedUser =
        AuthenticatedUser.builder()
            .id(user.getId())
            .username(user.getEmail())
            .displayName(user.getDisplayName())
            .authorities(authorities)
            .build();
    Authentication authentication =
        new AbstractAuthenticationToken(authorities) {
          @Override
          public @Nullable Object getCredentials() {
            return null;
          }

          @Override
          public @Nullable Object getPrincipal() {
            return authenticatedUser;
          }
        };

    String accessToken = tokenService.generateAccessToken(authentication);
    String refreshToken = tokenService.generateRefreshToken(authentication);

    sessionRepository.complete(session.getSessionId(), accessToken, refreshToken, user.getId());

    log.info("OAuth completed for user: {}", userInfo.email());
    try {
      return readTemplate(
          localeStrategies.get(LocaleConstant.VIETNAMESE).getSubject(TemplateType.LOGIN_OAUTH2));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String readTemplate(String templatePath) throws Exception {
    ClassPathResource resource = new ClassPathResource(templatePath);
    return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
  }

  @Override
  public GoogleOAuthStatusResponse checkStatus(String sessionId) {
    OAuthSession session = sessionRepository.findBySessionId(sessionId);

    if (session == null) {
      return new GoogleOAuthStatusResponse(GoogleAuthStatus.EXPIRED, null, null, null);
    }

    if (session.isSuccess()) {
      User user =
          userRepository
              .findById(session.getUserId())
              .orElseThrow(() -> new ResourceException(ErrorCode.NOT_FOUND));

      sessionRepository.remove(sessionId);
      user.setPasswordHash(null);
      user.setUpdated(null);
      user.setCreated(null);
      user.setBirthday(null);

      return new GoogleOAuthStatusResponse(
          GoogleAuthStatus.SUCCESS, session.getAccessToken(), session.getRefreshToken(), user);
    }

    return new GoogleOAuthStatusResponse(GoogleAuthStatus.PENDING, null, null, null);
  }

  private String buildAuthUrl(String state) {
    return configuration.getAuthEndpoint()
        + "?client_id="
        + configuration.getClientId()
        + "&redirect_uri="
        + encode(configuration.getRedirectUri())
        + "&response_type=code"
        + "&scope="
        + encode(configuration.getScopes().replace(",", " "))
        + "&state="
        + state
        + "&access_type=offline"
        + "&prompt=consent";
  }

  private GoogleTokenResponse exchangeCodeForTokens(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("client_id", configuration.getClientId());
    params.add("client_secret", configuration.getClientSecret());
    params.add("redirect_uri", configuration.getRedirectUri());
    params.add("grant_type", "authorization_code");

    ResponseEntity<GoogleTokenResponse> response =
        restTemplate.postForEntity(
            configuration.getTokenEndpoint(),
            new HttpEntity<>(params, headers),
            GoogleTokenResponse.class);

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new RuntimeException("Failed to exchange code for tokens");
    }
    return response.getBody();
  }

  private GoogleUserInfoResponse fetchUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<GoogleUserInfoResponse> response =
        restTemplate.exchange(
            configuration.getUserInfoEndpoint(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GoogleUserInfoResponse.class);

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new RuntimeException("Failed to fetch user info from Google");
    }

    return response.getBody();
  }

  private User findOrCreateUser(GoogleUserInfoResponse userInfo) {
    return userRepository
        .findByGoogleId(userInfo.sub())
        .orElseGet(
            () ->
                userRepository
                    .findByEmail(userInfo.email())
                    .map(user -> linkGoogleAccount(user, userInfo))
                    .orElseGet(() -> createNewUser(userInfo)));
  }

  private User linkGoogleAccount(User user, GoogleUserInfoResponse userInfo) {
    user.setGoogleId(userInfo.sub());
    user.setAuthProvider(AuthProvider.GOOGLE);
    return userRepository.save(user);
  }

  private User createNewUser(GoogleUserInfoResponse userInfo) {
    String displayName = userInfo.name().replace(" ", "");
    displayName = displayName.substring(0, Math.min(12, displayName.length()));
    User user =
        User.builder()
            .email(userInfo.email())
            .displayName(displayName)
            .googleId(userInfo.sub())
            .role(Role.USER)
            .authProvider(AuthProvider.GOOGLE)
            .build();
    return userRepository.save(user);
  }

  private String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
