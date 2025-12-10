package com.vn.nghlong3004.client.service;

import com.vn.nghlong3004.client.model.request.*;
import java.util.concurrent.CompletableFuture;

public interface HttpService {

  CompletableFuture<String> sendRegisterRequest(RegisterRequest registerRequest);

  CompletableFuture<String> sendLoginRequest(LoginRequest loginRequest);

  CompletableFuture<String> sendForgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  CompletableFuture<String> sendVerifyOTP(OTPRequest otpRequest);

  CompletableFuture<String> sendResetPassword(ResetPasswordRequest resetPasswordRequest);
}
