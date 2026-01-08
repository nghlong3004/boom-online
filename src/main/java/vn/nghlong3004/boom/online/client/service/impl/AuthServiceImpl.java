package vn.nghlong3004.boom.online.client.service.impl;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.MediaTypeConstant;
import vn.nghlong3004.boom.online.client.model.response.GoogleAuthInitResponse;
import vn.nghlong3004.boom.online.client.model.response.GoogleAuthStatusResponse;
import vn.nghlong3004.boom.online.client.model.response.LoginResponse;
import vn.nghlong3004.boom.online.client.service.AuthService;
import vn.nghlong3004.boom.online.client.util.BrowserUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 01/08/2026
 */
@Slf4j
public class AuthServiceImpl implements AuthService {

  private static final int POLL_INTERVAL_SECONDS = 5;
  private static final int MAX_POLL_ATTEMPTS = 60;
  private static final String HTTP_PREFIX = "http://";

  private final String serverUrl;
  private final Gson gson;
  private final HttpClient httpClient;

  private ScheduledExecutorService scheduler;
  private ScheduledFuture<?> pollingTask;
  private final AtomicBoolean cancelled = new AtomicBoolean(false);
  private final AtomicBoolean inProgress = new AtomicBoolean(false);

  public AuthServiceImpl(String serverUrl, Gson gson) {
    this.serverUrl = serverUrl;
    this.gson = gson;
    this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
  }

  @Override
  public void loginWithGoogle(Consumer<LoginResponse> onSuccess, Consumer<String> onError) {
    if (inProgress.getAndSet(true)) {
      log.warn("Google login already in progress");
      return;
    }
    cancelled.set(false);

    CompletableFuture.runAsync(() -> executeGoogleLogin(onSuccess, onError));
  }

  @Override
  public void cancelGoogleLogin() {
    cancelled.set(true);
    stopPolling();
    inProgress.set(false);
  }

  @Override
  public boolean isGoogleLoginInProgress() {
    return inProgress.get();
  }

  private void executeGoogleLogin(Consumer<LoginResponse> onSuccess, Consumer<String> onError) {
    try {
      // Step 1: Get auth URL from server
      GoogleAuthInitResponse init = initOAuthSession();
      if (init == null || init.authUrl() == null) {
        handleError(onError, "Failed to initialize Google login");
        stopPolling();
        return;
      }

      // Step 2: Open browser
      if (!BrowserUtil.openUrl(init.authUrl())) {
        handleError(onError, "Failed to open browser");
        return;
      }

      // Step 3: Poll for result
      startPolling(init.sessionId(), onSuccess, onError);

    } catch (Exception e) {
      handleError(onError, e.getMessage());
    }
  }

  private GoogleAuthInitResponse initOAuthSession() {
    try {
      String url = HTTP_PREFIX + serverUrl + "/auth/google/init";
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .header(MediaTypeConstant.NAME, MediaTypeConstant.APPLICATION_JSON_VALUE)
              .POST(HttpRequest.BodyPublishers.noBody())
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        return gson.fromJson(response.body(), GoogleAuthInitResponse.class);
      }
      return null;
    } catch (Exception e) {
      log.error("Failed to init OAuth", e);
      return null;
    }
  }

  private void startPolling(
      String sessionId, Consumer<LoginResponse> onSuccess, Consumer<String> onError) {
    scheduler = Executors.newSingleThreadScheduledExecutor();
    AtomicInteger attempts = new AtomicInteger(0);

    pollingTask =
        scheduler.scheduleAtFixedRate(
            () -> pollStatus(sessionId, attempts, onSuccess, onError),
            0,
            POLL_INTERVAL_SECONDS,
            TimeUnit.SECONDS);
  }

  private void pollStatus(
      String sessionId,
      AtomicInteger attempts,
      Consumer<LoginResponse> onSuccess,
      Consumer<String> onError) {
    if (cancelled.get()) {
      stopPolling();
      handleError(onError, "Login cancelled");
      return;
    }

    if (attempts.incrementAndGet() > MAX_POLL_ATTEMPTS) {
      stopPolling();
      handleError(onError, "Login timeout");
      return;
    }

    try {
      GoogleAuthStatusResponse status = checkStatus(sessionId);
      if (status == null) return;

      if (status.isSuccess()) {
        stopPolling();
        LoginResponse response =
            new LoginResponse(status.accessToken(), status.refreshToken(), status.user());
        inProgress.set(false);
        SwingUtilities.invokeLater(() -> onSuccess.accept(response));
      } else if (status.isExpired()) {
        stopPolling();
        handleError(onError, "Session expired");
      }
    } catch (Exception e) {
      log.error("Poll failed", e);
    }
  }

  private GoogleAuthStatusResponse checkStatus(String sessionId) {
    try {
      String url = HTTP_PREFIX + serverUrl + "/auth/google/status?sessionId=" + sessionId;
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        return gson.fromJson(response.body(), GoogleAuthStatusResponse.class);
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  private void handleError(Consumer<String> onError, String message) {
    inProgress.set(false);
    SwingUtilities.invokeLater(() -> onError.accept(message));
  }

  private void stopPolling() {
    if (pollingTask != null) pollingTask.cancel(true);
    if (scheduler != null) scheduler.shutdown();
  }
}
