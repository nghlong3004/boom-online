package com.vn.nghlong3004.client.context;

import lombok.Getter;
import lombok.Setter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
@Getter
@Setter
public class ApplicationContext {
  private String accessToken;

  private String refreshToken;

  private String verificationToken;

  private String email;

  public static ApplicationContext getInstance() {
    return HOLDER.INSTANCE;
  }

  private ApplicationContext() {}

  private static class HOLDER {
    private static final ApplicationContext INSTANCE = new ApplicationContext();
  }
}
