package vn.nghlong3004.boom.online.client.service.impl;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.MediaTypeConstant;
import vn.nghlong3004.boom.online.client.model.request.*;
import vn.nghlong3004.boom.online.client.service.HttpService;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
@Slf4j
@RequiredArgsConstructor
public class HttpServiceImpl implements HttpService {
  private static final String HTTP = "http";
  private final HttpClient client;
  private final Gson gson;
  private final String serverUrl;

  @Override
  public CompletableFuture<String> sendRegisterRequest(RegisterRequest registerRequest) {
    String url = HTTP + serverUrl + "/auth/register";
    log.info("Initiating registration to URL: {}", url);

    String jsonBody = gson.toJson(registerRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendLoginRequest(LoginRequest loginRequest) {
    String url = HTTP + serverUrl + "/auth/login";
    log.info("Initiating login request for email: {}", loginRequest.email());

    String jsonBody = gson.toJson(loginRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    String url = HTTP + serverUrl + "/auth/forgot-password";
    log.info("Initiating forgot password request for email: {}", forgotPasswordRequest.email());

    String jsonBody = gson.toJson(forgotPasswordRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendVerifyOTP(OTPRequest otpRequest) {
    String url = HTTP + serverUrl + "/auth/verify-otp";
    log.info("Initiating verify otp request for email: {}", otpRequest.email());

    String jsonBody = gson.toJson(otpRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendResetPassword(ResetPasswordRequest resetPasswordRequest) {
    String url = HTTP + serverUrl + "/auth/reset-password";
    log.info("Initiating reset password request for email: {}", resetPasswordRequest.email());

    String jsonBody = gson.toJson(resetPasswordRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  private HttpRequest buildRequest(String jsonBody, String url) {
    return HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header(MediaTypeConstant.NAME, MediaTypeConstant.APPLICATION_JSON_VALUE)
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build();
  }

  private String handleResponse(HttpResponse<String> response) {
    int statusCode = response.statusCode();
    String body = response.body();

    if (statusCode >= 200 && statusCode < 300) {
      log.debug("Request successful. Body: {}", body);
      return body;
    } else {
      log.error("Request failed. Status: {}, Body: {}", statusCode, body);
      throw new RuntimeException(body);
    }
  }
}
