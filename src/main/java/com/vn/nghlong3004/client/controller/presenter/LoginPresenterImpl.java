package com.vn.nghlong3004.client.controller.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.controller.LoginPresenter;
import com.vn.nghlong3004.client.controller.LoginView;
import com.vn.nghlong3004.client.model.request.LoginRequest;
import com.vn.nghlong3004.client.model.response.ErrorResponse;
import com.vn.nghlong3004.client.model.response.LoginResponse;
import com.vn.nghlong3004.client.service.HttpService;
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
public class LoginPresenterImpl implements LoginPresenter {

  private final LoginView view;
  private final HttpService httpService;
  private final Gson gson;

  @Override
  public void handleLogin() {
    String email = view.getEmail();
    String password = view.getPassword();

    if (email.isEmpty() || password.isEmpty()) {
      view.showError("login_empty_fields");
      return;
    }

    if (!isValidEmail(email)) {
      view.showError("invalid_email");
      return;
    }

    if (password.length() < 6) {
      view.showError("password_short");
      return;
    }

    view.showLoading(true);
    LoginRequest req = new LoginRequest(email, password);

    httpService
        .sendLoginRequest(req)
        .thenAccept(
            responseBody -> {
              view.showLoading(false);
              try {
                LoginResponse loginResponse = gson.fromJson(responseBody, LoginResponse.class);

                ApplicationContext.getInstance().setAccessToken(loginResponse.accessToken());
                ApplicationContext.getInstance().setRefreshToken(loginResponse.refreshToken());
                ApplicationContext.getInstance().setEmail(email);

                view.showSuccessMessage();
                view.clearForm();
                view.closeLoginModal();

              } catch (Exception e) {
                log.error(e.getLocalizedMessage());
                view.showError("server_error");
              }
            })
        .exceptionally(
            ex -> {
              view.showLoading(false);
              Throwable cause = ex.getCause();
              String rawMessage = (cause != null) ? cause.getMessage() : ex.getMessage();
              view.showError(mapErrorToMessageKey(rawMessage));
              return null;
            });
  }

  @Override
  public void onRegisterClicked() {
    view.openRegisterModal();
  }

  @Override
  public void onForgotPasswordClicked() {
    view.openForgotPasswordModal();
  }

  @Override
  public void onGoogleLoginClicked() {
    view.showInfo("This feature is not yet supported!");
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
        return "login_bad_credentials";
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
    }
    return "login_failed";
  }
}
