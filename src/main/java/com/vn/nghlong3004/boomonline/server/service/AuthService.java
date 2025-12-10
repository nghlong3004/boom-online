package com.vn.nghlong3004.boomonline.server.service;

import com.vn.nghlong3004.boomonline.server.model.request.*;
import com.vn.nghlong3004.boomonline.server.model.response.LoginResponse;
import com.vn.nghlong3004.boomonline.server.model.response.OTPResponse;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public interface AuthService {
  void register(RegisterRequest request);

  LoginResponse login(LoginRequest request);

  OTPResponse forgotPassword(ForgotPasswordRequest request);

  OTPResponse verifyOTP(OTPRequest request);

  void resetPassword(ResetPasswordRequest request);
}
