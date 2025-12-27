package vn.nghlong3004.boom.online.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.nghlong3004.boom.online.server.exception.ErrorCode;
import vn.nghlong3004.boom.online.server.exception.ResourceException;
import vn.nghlong3004.boom.online.server.service.TokenService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/25/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getRequestURI();
    log.info("Incoming request path: {}", path);
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    log.info(authHeader);
    String token = authHeader.substring(7);
    log.info("Extracted JWT token: {}", token);

    try {
      tokenService.validateAccessToken(token);
    } catch (ResourceException e) {
      log.info("Validate access token=false, message={}", e.getMessage());
      setResponse(response, e.getErrorCode());
      return;
    }

    var username = tokenService.getUsernameFromToken(token);
    log.info("JWT validated successfully for username={}", username);

    var user = userDetailsService.loadUserByUsername(username);
    var role = tokenService.getRole(token);
    log.info("Request with role={}", role);
    var auth =
        new UsernamePasswordAuthenticationToken(
            user,
            null,
            (role == null || role.isEmpty())
                ? user.getAuthorities()
                : AuthorityUtils.createAuthorityList(role));
    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(auth);
    filterChain.doFilter(request, response);
  }

  private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
    final var errorResponse = errorCode.toErrorResponse();
    response.setStatus(errorCode.getStatus());
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
