package com.vn.nghlong3004.client.service.impl;

import com.google.gson.Gson;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.constant.APIConstant;
import com.vn.nghlong3004.client.constant.MediaTypeConstant;
import com.vn.nghlong3004.client.model.request.*;
import com.vn.nghlong3004.client.service.HttpService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
@Slf4j
public class HttpServiceImpl implements HttpService {
  private final HttpClient client;
  private final Gson gson;
  private final String BASE_URL;

  public HttpServiceImpl() {
    this.BASE_URL = ApplicationConfiguration.getInstance().getServerUrl();
    this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    this.gson = ApplicationConfiguration.getInstance().getGson();
    log.info("HttpService initialized with Base URL: {}", BASE_URL);
  }

  @Override
  public CompletableFuture<String> sendRegisterRequest(RegisterRequest registerRequest) {
    String url = BASE_URL + APIConstant.REGISTER;
    log.info("Initiating registration to URL: {}", url);

    String jsonBody = gson.toJson(registerRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendLoginRequest(LoginRequest loginRequest) {
    String url = BASE_URL + APIConstant.LOGIN;
    log.info("Initiating login request for email: {}", loginRequest.email());

    String jsonBody = gson.toJson(loginRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    String url = BASE_URL + APIConstant.FORGOT_PASSWORD;
    log.info("Initiating forgot password request for email: {}", forgotPasswordRequest.email());

    String jsonBody = gson.toJson(forgotPasswordRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendVerifyOTP(OTPRequest otpRequest) {
    String url = BASE_URL + APIConstant.VERIFY_OTP;
    log.info("Initiating verify otp request for email: {}", otpRequest.email());

    String jsonBody = gson.toJson(otpRequest);

    HttpRequest request = buildRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendResetPassword(ResetPasswordRequest resetPasswordRequest) {
    String url = BASE_URL + APIConstant.RESET_PASSWORD;
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
