package vn.nghlong3004.boom.online.server.model;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
@Getter
@Setter
@Builder
public class OAuthSession {

  private String sessionId;
  private String state;
  private GoogleAuthStatus status;
  private String accessToken;
  private String refreshToken;
  private Long userId;
  private Instant expiresAt;

  public boolean isSuccess() {
    return GoogleAuthStatus.SUCCESS == status;
  }

  public boolean isExpired() {
    return GoogleAuthStatus.EXPIRED == status;
  }

  public boolean isPending() {
    return GoogleAuthStatus.PENDING == status;
  }
}
