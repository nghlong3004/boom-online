package vn.nghlong3004.boom.online.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.nghlong3004.boom.online.server.model.request.*;
import vn.nghlong3004.boom.online.server.model.response.GoogleOAuthInitResponse;
import vn.nghlong3004.boom.online.server.model.response.GoogleOAuthStatusResponse;
import vn.nghlong3004.boom.online.server.model.response.LoginResponse;
import vn.nghlong3004.boom.online.server.model.response.OTPResponse;
import vn.nghlong3004.boom.online.server.service.AuthService;
import vn.nghlong3004.boom.online.server.service.GoogleOAuthService;

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
  private final GoogleOAuthService googleOAuthService;

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
  public void forgotPassword(@Validated @RequestBody ForgotPasswordRequest request) {
    authService.forgotPassword(request);
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

  @PostMapping("/google/init")
  @ResponseStatus(HttpStatus.OK)
  public GoogleOAuthInitResponse initGoogleAuth() {
    return googleOAuthService.initializeAuth();
  }

  @GetMapping("/google/callback")
  public String handleGoogleCallback(@RequestParam String code, @RequestParam String state) {

    googleOAuthService.handleCallback(code, state);

    return """
        <!DOCTYPE html>
        <html>
        <head><title>Login Success</title></head>
        <body style="font-family:Arial;display:flex;justify-content:center;align-items:center;height:100vh;background:linear-gradient(135deg,#667eea,#764ba2);">
          <div style="text-align:center;background:white;padding:40px 60px;border-radius:16px;">
            <div style="font-size:64px;color:#4CAF50;">✓</div>
            <h1>Đăng nhập thành công!</h1>
            <p>Bạn có thể đóng tab này.</p>
          </div>
        </body>
        </html>
        """;
  }

  @GetMapping("/google/status")
  @ResponseStatus(HttpStatus.OK)
  public GoogleOAuthStatusResponse checkGoogleStatus(@RequestParam String sessionId) {
    return googleOAuthService.checkStatus(sessionId);
  }
}
