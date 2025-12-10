package com.vn.nghlong3004.boomonline.server.controller;

import com.vn.nghlong3004.boomonline.server.model.request.*;
import com.vn.nghlong3004.boomonline.server.model.response.LoginResponse;
import com.vn.nghlong3004.boomonline.server.model.response.OTPResponse;
import com.vn.nghlong3004.boomonline.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@Validated @RequestBody RegisterRequest request) {
    authService.register(request);
  }

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public LoginResponse login(@Validated @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping(value = "/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public OTPResponse forgotPassword(@Validated @RequestBody ForgotPasswordRequest request) {
    return authService.forgotPassword(request);
  }

  @PostMapping(value = "/verify-otp", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public OTPResponse verifyOTP(@Validated @RequestBody OTPRequest request) {
    return authService.verifyOTP(request);
  }

  @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request);
  }
}
