package vn.nghlong3004.boom.online.client.service.impl;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.MediaTypeConstant;
import vn.nghlong3004.boom.online.client.model.request.*;
import vn.nghlong3004.boom.online.client.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.client.model.room.Room;
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
  private final HttpClient client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
  private final String serverUrl;
  private final Gson gson;

  @Override
  public CompletableFuture<RoomPageResponse> getRooms(int page, int size, String token) {

    String url = HTTP + serverUrl + "/rooms?page=" + page + "&size=" + size;
    log.info("Fetching rooms from URL: {}", url);

    HttpRequest request = buildGetRequest(url, token);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse)
        .thenApply(jsonBody -> gson.fromJson(jsonBody, RoomPageResponse.class));
  }

  @Override
  public CompletableFuture<Room> createRoom(CreateRoomRequest createRoomRequest, String token) {
    String url = HTTP + serverUrl + "/rooms/create";
    log.info("Creating room at URL: {}", url);

    String jsonBody = gson.toJson(createRoomRequest);

    HttpRequest request = buildPostRequest(jsonBody, url, token);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse)
        .thenApply(json -> gson.fromJson(json, Room.class));
  }

  @Override
  public CompletableFuture<Room> joinRoom(String roomId, String token) {
    String url = HTTP + serverUrl + "/rooms/" + roomId + "/join";
    log.info("Joining room: {}", url);

    HttpRequest request = buildPostRequest("", url, token);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse)
        .thenApply(json -> gson.fromJson(json, Room.class));
  }

  @Override
  public CompletableFuture<String> sendRegisterRequest(RegisterRequest registerRequest) {
    String url = HTTP + serverUrl + "/auth/register";
    log.info("Initiating registration to URL: {}", url);

    String jsonBody = gson.toJson(registerRequest);

    HttpRequest request = buildAuthRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendLoginRequest(LoginRequest loginRequest) {
    String url = HTTP + serverUrl + "/auth/login";
    log.info("Initiating login request for email: {}", loginRequest.email());

    String jsonBody = gson.toJson(loginRequest);

    HttpRequest request = buildAuthRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    String url = HTTP + serverUrl + "/auth/forgot-password";
    log.info("Initiating forgot password request for email: {}", forgotPasswordRequest.email());

    String jsonBody = gson.toJson(forgotPasswordRequest);

    HttpRequest request = buildAuthRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendVerifyOTP(OTPRequest otpRequest) {
    String url = HTTP + serverUrl + "/auth/verify-otp";
    log.info("Initiating verify otp request for email: {}", otpRequest.email());

    String jsonBody = gson.toJson(otpRequest);

    HttpRequest request = buildAuthRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  @Override
  public CompletableFuture<String> sendResetPassword(ResetPasswordRequest resetPasswordRequest) {
    String url = HTTP + serverUrl + "/auth/reset-password";
    log.info("Initiating reset password request for email: {}", resetPasswordRequest.email());

    String jsonBody = gson.toJson(resetPasswordRequest);

    HttpRequest request = buildAuthRequest(jsonBody, url);

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(this::handleResponse);
  }

  private HttpRequest buildGetRequest(String url, String token) {
    return HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Bearer " + token)
        .header("Accept", MediaTypeConstant.APPLICATION_JSON_VALUE)
        .GET()
        .build();
  }

  private HttpRequest buildPostRequest(String jsonBody, String url, String token) {
    return HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Bearer " + token)
        .header(MediaTypeConstant.NAME, MediaTypeConstant.APPLICATION_JSON_VALUE)
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build();
  }

  private HttpRequest buildAuthRequest(String jsonBody, String url) {
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
