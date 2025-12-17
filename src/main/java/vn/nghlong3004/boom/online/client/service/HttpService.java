package vn.nghlong3004.boom.online.client.service;

import java.util.concurrent.CompletableFuture;
import vn.nghlong3004.boom.online.client.model.request.*;

public interface HttpService {

  CompletableFuture<String> sendRegisterRequest(RegisterRequest registerRequest);

  CompletableFuture<String> sendLoginRequest(LoginRequest loginRequest);

  CompletableFuture<String> sendForgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  CompletableFuture<String> sendVerifyOTP(OTPRequest otpRequest);

  CompletableFuture<String> sendResetPassword(ResetPasswordRequest resetPasswordRequest);
}
