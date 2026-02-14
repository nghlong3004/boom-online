package vn.nghlong3004.boom.online.client.session;

import lombok.Getter;
import vn.nghlong3004.boom.online.client.model.User;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Getter
public class UserSession {

  private String accessToken;

  private String refreshToken;

  private User currentUser;

  private UserSession() {}

  public static UserSession getInstance() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    private static final UserSession INSTANCE = new UserSession();
  }

  public void setSession(String accessToken, String refreshToken, User user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.currentUser = user;
  }

  public void clear() {
    this.accessToken = null;
    this.refreshToken = null;
    this.currentUser = null;
  }

  public boolean isLoggedIn() {
    return accessToken != null;
  }
}
