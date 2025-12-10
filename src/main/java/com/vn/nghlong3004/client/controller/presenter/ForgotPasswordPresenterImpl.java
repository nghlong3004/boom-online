package com.vn.nghlong3004.client.controller.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.controller.ForgotPasswordPresenter;
import com.vn.nghlong3004.client.controller.ForgotPasswordView;
import com.vn.nghlong3004.client.model.request.ForgotPasswordRequest;
import com.vn.nghlong3004.client.model.request.OTPRequest;
import com.vn.nghlong3004.client.model.response.ErrorResponse;
import com.vn.nghlong3004.client.model.response.OTPResponse;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordPresenterImpl implements ForgotPasswordPresenter {
  private final ForgotPasswordView view;
  private final HttpService httpService;
  private final Gson gson;

  private Timer timer;
  private boolean isTimerRunning = false;

  @Override
  public void handleSendOtp() {
    if (isTimerRunning) {
      return;
    }

    String email = view.getEmail().trim();

    if (!isValidEmail(email)) {
      view.showError("invalid_email");
      return;
    }

    view.showInfo("handler");

    String lang = LanguageUtil.getInstance().getCurrentLocale().getLanguage();
    ForgotPasswordRequest request = new ForgotPasswordRequest(email, lang);

    startCountDown();

    httpService
        .sendForgotPassword(request)
        .thenAccept(
            responseBody -> {
              try {
                OTPResponse response = gson.fromJson(responseBody, OTPResponse.class);
                ApplicationContext.getInstance().setVerificationToken(response.token());
                ApplicationContext.getInstance().setEmail(email);

                view.showInfo("forgot_password_send_otp");
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

  @Override
  public void handleSubmit() {
    view.showInfo("handler");

    String email = ApplicationContext.getInstance().getEmail();
    String token = view.getOtp().trim();

    if (token.length() != 8 || !token.matches("\\d+")) {
      view.showWarning("forgot_password_match_otp");
      return;
    }

    OTPRequest request = new OTPRequest(email, token);

    httpService
        .sendVerifyOTP(request)
        .thenAccept(
            responseBody -> {
              OTPResponse response = gson.fromJson(responseBody, OTPResponse.class);
              ApplicationContext.getInstance().setVerificationToken(response.token());

              view.showInfo("reset_password_title");
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

  @Override
  public void onBackToLoginClicked() {
    view.showInfo("login_button_login");
    cleanup();
    view.clearForm();
    view.navigateToLogin();
  }

  @Override
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
        100);
  }

  private void stopTimer() {
    isTimerRunning = false;
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
    view.setOtpButtonText("forgot_password_btn_otp");
  }

  private boolean isValidEmail(String email) {
    String emailRegex =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pat = Pattern.compile(emailRegex);
    return email != null && pat.matcher(email).matches();
  }

  private String mapErrorToMessageKey(String errorBody) {
    if (errorBody == null) return "server_error";

    if (errorBody.contains("ConnectException") || errorBody.contains("Network")) {
      return "server_error";
    }

    try {
      ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);

      if (errorResponse != null && errorResponse.code() != null) {
        return switch (errorResponse.code()) {
          case "EmailIncorrect" -> "forgot_password_send_otp_email_incorrect";
          case "parameter_required", "OtpExpired", "OtpIncorrect", "OtpNotFound" ->
              "forgot_password_otp_invalid";
          default -> "unknown_error";
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
      return "server_error";
    }

    return "login_failed";
  }
}
