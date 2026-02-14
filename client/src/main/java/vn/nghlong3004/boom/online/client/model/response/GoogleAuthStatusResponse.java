package vn.nghlong3004.boom.online.client.model.response;

import vn.nghlong3004.boom.online.client.model.GoogleAuthStatus;
import vn.nghlong3004.boom.online.client.model.User;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
public record GoogleAuthStatusResponse(
    GoogleAuthStatus status, String accessToken, String refreshToken, User user) {
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
