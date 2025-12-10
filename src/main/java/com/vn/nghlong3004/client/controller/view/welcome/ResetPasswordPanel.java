package com.vn.nghlong3004.client.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.model.request.ResetPasswordRequest;
import com.vn.nghlong3004.client.model.response.ErrorResponse;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import com.vn.nghlong3004.client.view.component.ButtonLink;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
@Slf4j
public class ResetPasswordPanel extends FormPanel {

  public ResetPasswordPanel(HttpService httpService, Gson gson) {
    super(httpService, gson);
    setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));
    JTextArea text = new JTextArea(getText("reset_password_description"));
    text.setEditable(false);
    text.setFocusable(false);
    text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
    add(text);

    add(new JSeparator(), "gapy 15 15");

    JLabel lbPassword = new JLabel(getText("reset_password_holder_place_password"));
    lbPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbPassword, "gapy 10 n");
    JPasswordField txtPassword = new JPasswordField();
    installRevealButton(txtPassword);
    txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getText("login_password"));
    add(txtPassword);

    JLabel lbRePassword = new JLabel(getText("reset_password_holder_place_re_password"));
    lbRePassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbRePassword, "gapy 10 n");

    JPasswordField txtRePassword = new JPasswordField();
    installRevealButton(txtRePassword);
    txtRePassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        getText("reset_password_holder_place_re_password_text"));
    add(txtRePassword);

    JButton cmdSubmit = new ButtonLink("Submit");
    cmdSubmit.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");

    add(cmdSubmit, "gapy 15 15");
    cmdSubmit.addActionListener(
        actionEvent -> {
          String password = new String(txtPassword.getPassword());
          String rePassword = new String(txtRePassword.getPassword());

          if (password.length() <= 6) {
            NotificationUtil.getInstance()
                .show(this, Toast.Type.WARNING, getText("validation_password_length_error"));
            return;
          }

          if (!password.equals(rePassword)) {
            NotificationUtil.getInstance()
                .show(this, Toast.Type.WARNING, getText("validation_password_match_error"));
            return;
          }

          NotificationUtil.getInstance()
              .show(this, Toast.Type.INFO, LanguageUtil.getInstance().getString("handler"));

          String email = ApplicationContext.getInstance().getEmail();
          String token = ApplicationContext.getInstance().getVerificationToken();
          String lang = LanguageUtil.getInstance().getCurrentLocale().getLanguage();

          ResetPasswordRequest request = new ResetPasswordRequest(token, email, password, lang);

          cmdSubmit.setEnabled(false);
          httpService
              .sendResetPassword(request)
              .thenAccept(
                  responseBody -> {
                    cmdSubmit.setEnabled(true);
                    NotificationUtil.getInstance()
                        .show(
                            this,
                            Toast.Type.SUCCESS,
                            LanguageUtil.getInstance().getString("register_successfully"));
                    ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId());
                  })
              .exceptionally(
                  e -> {
                    Throwable throwable = e.getCause();
                    String rawMessage =
                        (throwable != null) ? throwable.getMessage() : e.getMessage();
                    String messageKey = mapErrorToMessageKey(rawMessage);
                    cmdSubmit.setEnabled(true);
                    showError(messageKey);
                    return null;
                  });
        });
  }

  private void showError(String langKey) {
    NotificationUtil.getInstance()
        .show(this, Toast.Type.ERROR, LanguageUtil.getInstance().getString(langKey));
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
          case "TokenIncorrect" -> "reset_password_bad_credentials";
          default -> "unknown_error";
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
      return "server_error";
    }

    return "login_failed";
  }

  private String getText(String key) {
    return LanguageUtil.getInstance().getString(key);
  }
}
