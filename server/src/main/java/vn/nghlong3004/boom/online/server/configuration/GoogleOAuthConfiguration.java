package vn.nghlong3004.boom.online.server.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.oauth2")
public class GoogleOAuthConfiguration {

  private String clientId;
  private String clientSecret;
  private String redirectUri;
  private String scopes;

  private static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
  private static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
  private static final String USERINFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";

  public String getAuthEndpoint() {
    return AUTH_ENDPOINT;
  }

  public String getTokenEndpoint() {
    return TOKEN_ENDPOINT;
  }

  public String getUserInfoEndpoint() {
    return USERINFO_ENDPOINT;
  }
}
