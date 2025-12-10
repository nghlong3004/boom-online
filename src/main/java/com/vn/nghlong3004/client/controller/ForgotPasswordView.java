package com.vn.nghlong3004.client.controller;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public interface ForgotPasswordView {
  void showLoading(boolean isLoading);

  void showSuccess(String messageKey);

  void showInfo(String messageKey);

  void showWarning(String messageKey);

  void showError(String messageKey);

  void setOtpButtonText(String text);

  void setOtpButtonEnabled(boolean enabled);

  void setSubmitEnabled(boolean enabled);

  void setOtpFieldEnabled(boolean enabled);

  void navigateToLogin();

  void navigateToResetPassword();

  String getEmail();

  String getOtp();

  void clearForm();
}
