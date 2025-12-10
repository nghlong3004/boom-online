package com.vn.nghlong3004.client.controller;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public interface LoginView {
  void showLoading(boolean isLoading);

  void showSuccessMessage();

  void showError(String messageKey);

  void showInfo(String message);

  void closeLoginModal();

  void openRegisterModal();

  void openForgotPasswordModal();

  String getEmail();

  String getPassword();

  void clearForm();
}
