package vn.nghlong3004.boom.online.client.session;

import lombok.Getter;
import lombok.Setter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Getter
@Setter
public class ResetPasswordSession {

  private String email;
  private String token;

  public void clear() {
    email = null;
    token = null;
  }

  public static ResetPasswordSession getInstance() {
    return ResetPasswordSession.Holder.INSTANCE;
  }

  private static class Holder {
    private static final ResetPasswordSession INSTANCE = new ResetPasswordSession();
  }
}
