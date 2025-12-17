package vn.nghlong3004.boom.online.client.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.model.request.ForgotPasswordRequest;
import vn.nghlong3004.boom.online.client.model.request.OTPRequest;
import vn.nghlong3004.boom.online.client.model.response.ErrorResponse;
import vn.nghlong3004.boom.online.client.model.response.OTPResponse;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.session.ResetPasswordSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.view.welcome.ForgotPasswordPanel;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordPresenter {
  private final ForgotPasswordPanel view;
  private final HttpService httpService;
  private final Gson gson;

  private Timer timer;
  private boolean isTimerRunning = false;

  public void handleSendOtp() {
    if (isTimerRunning) {
      return;
    }

    String email = view.getEmail().trim();

    if (!isValidEmail(email)) {
      view.showError("valid.email");
      return;
    }

    view.showInfo("common.processing");

    String lang = I18NUtil.getCurrentLanguage();
    ForgotPasswordRequest request = new ForgotPasswordRequest(email, lang);

    startCountDown();

    httpService
        .sendForgotPassword(request)
        .thenAccept(
            responseBody -> {
              try {
                ResetPasswordSession.getInstance().setEmail(email);
                view.showInfo("forgot.msg.otp_sent");
                view.setSubmitEnabled(true);
                view.setOtpFieldEnabled(true);
              } catch (Exception e) {
                log.error("Error parsing OTP response", e);
              }
            })
        .exceptionally(
            e -> {
              stopTimer();
              Throwable throwable = e.getCause();
              String rawMessage = (throwable != null) ? throwable.getMessage() : e.getMessage();
              String messageKey = mapErrorToMessageKey(rawMessage);
              view.showError(messageKey);
              return null;
            });
  }

  public void handleSubmit() {
    view.showInfo("common.processing");

    String email = ResetPasswordSession.getInstance().getEmail();
    String token = view.getOtp().trim();

    if (token.length() != 8 || !token.matches("\\d+")) {
      view.showWarning("valid.otp.format");
      return;
    }

    OTPRequest request = new OTPRequest(email, token);

    httpService
        .sendVerifyOTP(request)
        .thenAccept(
            responseBody -> {
              OTPResponse response = gson.fromJson(responseBody, OTPResponse.class);
              ResetPasswordSession.getInstance().setToken(response.token());

              view.showInfo("reset.title");
              stopTimer();
              view.navigateToResetPassword();
            })
        .exceptionally(
            e -> {
              Throwable throwable = e.getCause();
              String rawMessage = (throwable != null) ? throwable.getMessage() : e.getMessage();
              String messageKey = mapErrorToMessageKey(rawMessage);
              view.showError(messageKey);
              return null;
            });
  }

  public void onBackToLoginClicked() {
    view.showInfo("login.btn.submit");
    cleanup();
    view.clearForm();
    view.navigateToLogin();
  }

  public void cleanup() {
    stopTimer();
  }

  private void startCountDown() {
    stopTimer();
    isTimerRunning = true;

    LocalDateTime startTime = LocalDateTime.now().plus(Duration.ofSeconds(60 * 5));
    timer = new Timer();

    timer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            Duration duration = Duration.between(LocalDateTime.now(), startTime);
            long seconds = duration.getSeconds();

            if (seconds <= 0) {
              stopTimer();
            } else {
              long displaySeconds = seconds % (60 * 5);
              String zero = (displaySeconds < 10) ? "0" : "";
              view.setOtpButtonText(zero + displaySeconds);
            }
          }
        },
        0,
        1000);
  }

  private void stopTimer() {
    isTimerRunning = false;
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
    view.setOtpButtonText("forgot.btn.send_otp");
  }

  private boolean isValidEmail(String email) {
    String emailRegex =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pat = Pattern.compile(emailRegex);
    return email != null && pat.matcher(email).matches();
  }

  private String mapErrorToMessageKey(String errorBody) {
    if (errorBody == null) return "common.error.server";

    if (errorBody.contains("ConnectException") || errorBody.contains("Network")) {
      return "common.error.server";
    }

    try {
      ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);

      if (errorResponse != null && errorResponse.code() != null) {
        return switch (errorResponse.code()) {
          case "EmailIncorrect" -> "forgot.error.email_not_found";
          case "parameter_required", "OtpExpired", "OtpIncorrect", "OtpNotFound" ->
              "forgot.error.otp_invalid";
          default -> "common.error.unknown";
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
      return "common.error.server";
    }

    return "common.error.unknown";
  }
}
