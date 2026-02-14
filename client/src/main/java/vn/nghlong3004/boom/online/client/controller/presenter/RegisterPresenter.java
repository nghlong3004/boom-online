package vn.nghlong3004.boom.online.client.controller.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.controller.view.welcome.RegisterPanel;
import vn.nghlong3004.boom.online.client.model.request.RegisterRequest;
import vn.nghlong3004.boom.online.client.model.response.ErrorResponse;
import vn.nghlong3004.boom.online.client.service.HttpService;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
@Slf4j
@RequiredArgsConstructor
public class RegisterPresenter {

  private final RegisterPanel view;
  private final HttpService httpService;
  private final Gson gson;

  public void handleRegister() {
    String email = view.getEmail();
    String password = view.getPassword();
    String rePassword = view.getRePassword();
    String displayName = view.getFullName();
    String birthday = view.getBirthday();
    int gender = view.getGender();

    if (!validateInput(email, password, rePassword, displayName, birthday, gender)) {
      return;
    }

    view.showLoading(true);
    RegisterRequest request = new RegisterRequest(email, password, birthday, displayName, gender);

    httpService
        .sendRegisterRequest(request)
        .thenAccept(
            response -> {
              view.showLoading(false);
              view.showSuccess("register.msg.success");
              view.setAccount();
              onLoginClicked();
            })
        .exceptionally(
            ex -> {
              view.showLoading(false);
              Throwable cause = ex.getCause();
              String rawMessage = (cause != null) ? cause.getMessage() : ex.getMessage();
              String messageKey = mapErrorToMessageKey(rawMessage);
              view.showWarning(messageKey);
              return null;
            });
  }

  public void onLoginClicked() {
    view.navigateToLogin();
    view.clearForm();
  }

  private boolean validateInput(
      String email, String pass, String rePass, String displayName, String birthday, int gender) {
    if (email.isBlank()
        || pass.isBlank()
        || rePass.isBlank()
        || displayName.isBlank()
        || birthday.isBlank()) {
      view.showWarning("register.error.empty_fields");
      return false;
    }

    if (gender == -1) {
      view.showWarning("register.error.empty_fields");
      return false;
    }

    if (!isValidEmail(email)) {
      view.showWarning("valid.email");
      return false;
    }

    if (!isValidDate(birthday)) {
      view.showWarning("register.error.invalid_dob");
      return false;
    }

    if (pass.length() < 6) {
      view.showWarning("valid.password.length");
      return false;
    }

    if (!pass.equals(rePass)) {
      view.showWarning("register.error.pass_mismatch");
      return false;
    }

    if (displayName.length() <= 2) {
      view.showWarning("register.error.display.length.min");
      return false;
    }

    if (displayName.length() > 12) {
      view.showWarning("register.error.display.length.max");
      return false;
    }

    return true;
  }

  private boolean isValidEmail(String email) {
    String emailRegex =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pat = Pattern.compile(emailRegex);
    return email != null && pat.matcher(email).matches();
  }

  private boolean isValidDate(String dateStr) {
    if (dateStr == null || dateStr.length() != 10) {
      return false;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    sdf.setLenient(false);

    try {
      Date date = sdf.parse(dateStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      int year = cal.get(Calendar.YEAR);
      int currentYear = Calendar.getInstance().get(Calendar.YEAR);
      return year >= 1900 && year < currentYear;
    } catch (ParseException e) {
      return false;
    }
  }

  private String mapErrorToMessageKey(String errorBody) {
    if (errorBody == null) return "common.error.unknown";

    if (errorBody.contains("ConnectException") || errorBody.contains("Network is unreachable")) {
      return "common.error.server";
    }

    try {
      ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);
      if (errorResponse != null && errorResponse.code() != null) {
        return switch (errorResponse.code()) {
          case "EmailAlready" -> "register.error.email_exists";
          case "InvalidRequest" -> "register.error.empty_fields";
          default -> "common.error.unknown";
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
      return "common.error.unknown";
    }
    return "common.error.unknown";
  }
}
