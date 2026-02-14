package vn.nghlong3004.boom.online.client.controller.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.controller.view.welcome.ResetPasswordPanel;
import vn.nghlong3004.boom.online.client.model.request.ResetPasswordRequest;
import vn.nghlong3004.boom.online.client.model.response.ErrorResponse;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.session.ResetPasswordSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Slf4j
@RequiredArgsConstructor
public class ResetPasswordPresenter {
  private final ResetPasswordPanel view;
  private final HttpService httpService;
  private final Gson gson;

  public void handleSubmit() {
    String password = view.getPassword();
    String rePassword = view.getRePassword();

    if (password.length() <= 6) {
      view.showWarning("valid.password.length");
      return;
    }

    if (!password.equals(rePassword)) {
      view.showWarning("valid.password.match");
      return;
    }

    view.showInfo("common.processing");
    view.showLoading(true);

    String email = ResetPasswordSession.getInstance().getEmail();
    String token = ResetPasswordSession.getInstance().getToken();
    String lang = I18NUtil.getCurrentLanguage();

    ResetPasswordRequest request = new ResetPasswordRequest(token, email, password, lang);

    httpService
        .sendResetPassword(request)
        .thenAccept(
            responseBody -> {
              ResetPasswordSession.getInstance().clear();
              view.showLoading(false);
              view.showSuccess("reset.msg.success");
              view.closeModal();
              view.clearForm();
            })
        .exceptionally(
            e -> {
              view.showLoading(false);
              Throwable throwable = e.getCause();
              String rawMessage = (throwable != null) ? throwable.getMessage() : e.getMessage();
              String messageKey = mapErrorToMessageKey(rawMessage);
              view.showError(messageKey);
              return null;
            });
  }

  private String mapErrorToMessageKey(String errorBody) {
    if (errorBody == null) return "common.error.unknown";

    if (errorBody.contains("ConnectException") || errorBody.contains("Network")) {
      return "common.error.server";
    }

    try {
      ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);

      if (errorResponse != null && errorResponse.code() != null) {
        return switch (errorResponse.code()) {
          case "TokenIncorrect" -> "reset.error.failed";
          default -> "common.error.unknown";
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
      return "common.error.server";
    }

    return "reset.error.failed";
  }
}
