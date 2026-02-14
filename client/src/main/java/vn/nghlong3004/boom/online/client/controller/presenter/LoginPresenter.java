package vn.nghlong3004.boom.online.client.controller.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import raven.modal.ModalDialog;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.controller.view.welcome.LoginPanel;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.request.LoginRequest;
import vn.nghlong3004.boom.online.client.model.response.ErrorResponse;
import vn.nghlong3004.boom.online.client.model.response.LoginResponse;
import vn.nghlong3004.boom.online.client.service.AuthService;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
@Slf4j
@RequiredArgsConstructor
public class LoginPresenter {

  private final LoginPanel view;
  private final HttpService httpService;
  private final AuthService authService;
  private final Gson gson;

  public void handleLogin() {
    String email = view.getEmail();
    String password = view.getPassword();

    if (email.isEmpty() || password.isEmpty()) {
      view.showError("login.error.empty");
      return;
    }

    if (!isValidEmail(email)) {
      view.showError("valid.email");
      return;
    }

    if (password.length() < 6) {
      view.showError("valid.password.length");
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
                UserSession.getInstance()
                    .setSession(
                        loginResponse.accessToken(),
                        loginResponse.refreshToken(),
                        loginResponse.user());
                ApplicationSession.getInstance().setOfflineMode(false);

                view.showSuccess("login.msg.success");
                view.clearForm();
                view.closeLoginModal();
                GameContext.getInstance().next();
              } catch (Exception e) {
                log.error(e.getLocalizedMessage());
                view.showError("common.error.server");
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

  public void onPlayingNowClicked() {
    ApplicationSession.getInstance().setOfflineMode(true);
    User user = User.builder().id(1L).displayName(GameConstant.DISPLAY_NAME_DEFAULT).build();
    UserSession.getInstance().setSession(null, null, user);
    ModalDialog.closeModal(ApplicationSession.getInstance().getWelcomeId());
    GameContext.getInstance().next();
    view.clearForm();
  }

  public void onRegisterClicked() {
    view.openRegisterModal();
    view.clearForm();
  }

  public void onForgotPasswordClicked() {
    view.openForgotPasswordModal();
    view.clearForm();
  }

  public void onGoogleLoginClicked() {
    view.showLoading(true);
    view.showInfo("login.google.opening_browser");

    authService.loginWithGoogle(
        loginResponse -> {
          view.showLoading(false);
          UserSession.getInstance()
              .setSession(
                  loginResponse.accessToken(), loginResponse.refreshToken(), loginResponse.user());
          ApplicationSession.getInstance().setOfflineMode(false);
          view.showSuccess("login.msg.success");
          view.closeLoginModal();
          GameContext.getInstance().next();
        },
        error -> {
          view.showLoading(false);
          view.showError("login.google.failed");
        });
  }

  public void setAccount(String email, String password) {
    view.setEmail(email);
    view.setPassword(password);
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
        return "login.error.bad_credentials";
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
    }
    return "login.msg.failed";
  }
}
