package com.vn.nghlong3004.client.controller;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public interface ForgotPasswordPresenter {
  void handleSendOtp();

  void handleSubmit();

  void onBackToLoginClicked();

  void cleanup();
}
