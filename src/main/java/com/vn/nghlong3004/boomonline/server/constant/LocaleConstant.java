package com.vn.nghlong3004.boomonline.server.constant;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
public final class LocaleConstant {
  public static final String VIETNAMESE = "vi";
  public static final String ENGLISH = "en";

  public static final String OTP_TEMPLATE_VI = "templates/otp-password-vi.html";
  public static final String OTP_SUBJECT_VI = "Mã xác nhận OTP [Boom Online]";

  public static final String OTP_TEMPLATE_EN = "templates/otp-password-en.html";
  public static final String OTP_SUBJECT_EN = "OTP Verification [Boom Online]";

  public static final String WELCOME_TEMPLATE_VI = "templates/welcome-vi.html";
  public static final String WELCOME_SUBJECT_VI = "Chào mừng đến với Boom Online";

  public static final String WELCOME_TEMPLATE_EN = "templates/welcome-en.html";
  public static final String WELCOME_SUBJECT_EN = "Welcome to Boom Online";

  public static final String RESET_SUCCESS_TEMPLATE_VI = "templates/reset-success-vi.html";
  public static final String RESET_SUCCESS_SUBJECT_VI = "Cảnh báo: Mật khẩu đã thay đổi thành công";

  public static final String RESET_SUCCESS_TEMPLATE_EN = "templates/reset-success-en.html";
  public static final String RESET_SUCCESS_SUBJECT_EN =
      "Security Alert: Password Changed Successfully";

  private LocaleConstant() {
    throw new UnsupportedOperationException("No instantiation");
  }
}
