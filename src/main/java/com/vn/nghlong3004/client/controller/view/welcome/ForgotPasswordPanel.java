package com.vn.nghlong3004.client.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.model.request.ForgotPasswordRequest;
import com.vn.nghlong3004.client.model.request.OTPRequest;
import com.vn.nghlong3004.client.model.response.ErrorResponse;
import com.vn.nghlong3004.client.model.response.OTPResponse;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import com.vn.nghlong3004.client.view.CustomModalBorder;
import com.vn.nghlong3004.client.view.component.ButtonLink;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import javax.swing.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/7/2025
 */
@Slf4j
public class ForgotPasswordPanel extends FormPanel {
  private final JTextField txtEmail;
  @Setter private CustomModalBorder resetPasswordPanel;

  private boolean show = true;
  private JButton button;
  private Timer timer;

  public ForgotPasswordPanel(HttpService httpService, Gson gson) {
    super(httpService, gson);
    setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));

    JTextArea text =
        new JTextArea(LanguageUtil.getInstance().getString("forgot_password_description"));
    text.setEditable(false);
    text.setFocusable(false);
    text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
    add(text);

    add(new JSeparator(), "gapy 15 15");

    JLabel lbEmail = new JLabel("Email");
    lbEmail.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbEmail);

    txtEmail = new JTextField();
    txtEmail.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        LanguageUtil.getInstance().getString("login_username"));
    add(txtEmail);

    JLabel lbOTP = new JLabel("OTP");
    lbOTP.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbOTP);

    JTextField txtOTP = new JTextField();
    txtOTP.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        LanguageUtil.getInstance().getString("forgot_password_title_otp"));
    add(txtOTP);

    JButton cmdSubmit = new ButtonLink("Submit");
    cmdSubmit.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");

    add(cmdSubmit, "gapy 15 15");

    cmdSubmit.setEnabled(false);
    txtOTP.setEnabled(false);

    ButtonLink cmdBackLogin =
        new ButtonLink(LanguageUtil.getInstance().getString("forgot_password_login"));
    add(cmdBackLogin, "grow 0,al center");
    installRevealButton(txtEmail, txtOTP, cmdSubmit);
    // event
    cmdSubmit.addActionListener(
        actionEvent -> {
          show(Toast.Type.INFO, LanguageUtil.getInstance().getString("handler"));
          String email = ApplicationContext.getInstance().getEmail();
          String token = txtOTP.getText().trim();
          if (token.length() != 8 || !token.matches("\\d+")) {
            NotificationUtil.getInstance()
                .show(
                    this,
                    Toast.Type.WARNING,
                    LanguageUtil.getInstance().getString("forgot_password_match_otp"));
            return;
          }
          OTPRequest request = new OTPRequest(email, token);
          httpService
              .sendVerifyOTP(request)
              .thenAccept(
                  responseBody -> {
                    OTPResponse response = gson.fromJson(responseBody, OTPResponse.class);
                    ApplicationContext.getInstance().setVerificationToken(response.token());
                    SwingUtilities.invokeLater(
                        () ->
                            show(
                                Toast.Type.INFO,
                                LanguageUtil.getInstance().getString("reset_password_title")));
                    button.setText(LanguageUtil.getInstance().getString("forgot_password_btn_otp"));
                    timer.stop();
                    show = true;
                    ModalDialog.pushModal(
                        resetPasswordPanel, ApplicationConfiguration.getInstance().getLoginId());
                  })
              .exceptionally(
                  e -> {
                    Throwable throwable = e.getCause();
                    String rawMessage =
                        (throwable != null) ? throwable.getMessage() : e.getMessage();
                    String messageKey = mapErrorToMessageKey(rawMessage);
                    showError(messageKey);
                    return null;
                  });
        });

    cmdBackLogin.addActionListener(
        actionEvent -> {
          NotificationUtil.getInstance()
              .show(
                  this,
                  Toast.Type.INFO,
                  LanguageUtil.getInstance().getString("login_button_login"));
          txtEmail.setText("");
          txtOTP.setText("");
          cmdSubmit.setEnabled(false);
          txtOTP.setEnabled(false);
          ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId());
        });
  }

  private boolean isValidEmail(String email) {
    String emailRegex =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pat = Pattern.compile(emailRegex);
    return email != null && pat.matcher(email).matches();
  }

  private void installRevealButton(JTextField txt, JTextField txtOTP, JButton cmdSubmit) {
    JToolBar toolBar = new JToolBar();
    toolBar.putClientProperty(FlatClientProperties.STYLE, "margin:0,0,0,5;");
    button = new JButton(LanguageUtil.getInstance().getString("forgot_password_btn_otp"));
    button.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent actionEvent) {
            if (show) {
              show(Toast.Type.INFO, LanguageUtil.getInstance().getString("handler"));
              String email = txtEmail.getText().trim();
              if (!isValidEmail(email)) {
                showError("invalid_email");
                return;
              }
              ForgotPasswordRequest request =
                  new ForgotPasswordRequest(
                      email, LanguageUtil.getInstance().getCurrentLocale().getLanguage());
              LocalDateTime startTime = LocalDateTime.now().plus(Duration.ofSeconds(60 * 5));
              timer =
                  new Timer(
                      100,
                      e -> {
                        Duration duration = Duration.between(LocalDateTime.now(), startTime);
                        long second = duration.getSeconds();
                        int seconds = (int) second % (60 * 5);
                        String zero = "";
                        if (seconds < 10) {
                          zero = "0";
                        }
                        button.setText(zero + second);
                        if (seconds == 0) {
                          show = true;
                          button.setText(
                              LanguageUtil.getInstance().getString("forgot_password_btn_otp"));
                          timer.stop();
                        }
                      });
              timer.start();
              httpService
                  .sendForgotPassword(request)
                  .thenAccept(
                      responseBody -> {
                        OTPResponse response = gson.fromJson(responseBody, OTPResponse.class);
                        ApplicationContext.getInstance().setVerificationToken(response.token());
                        ApplicationContext.getInstance().setEmail(email);
                        SwingUtilities.invokeLater(
                            () ->
                                show(
                                    Toast.Type.INFO,
                                    LanguageUtil.getInstance()
                                        .getString("forgot_password_send_otp")));
                        cmdSubmit.setEnabled(true);
                        txtOTP.setEnabled(true);
                        show = false;
                      })
                  .exceptionally(
                      e -> {
                        Throwable throwable = e.getCause();
                        String rawMessage =
                            (throwable != null) ? throwable.getMessage() : e.getMessage();
                        String messageKey = mapErrorToMessageKey(rawMessage);
                        SwingUtilities.invokeLater(
                            () -> {
                              showError(messageKey);
                              show = true;
                              button.setText(
                                  LanguageUtil.getInstance().getString("forgot_password_btn_otp"));
                              timer.stop();
                            });
                        return null;
                      });
            }
          }
        });
    toolBar.add(button);
    txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
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

  private void showError(String langKey) {
    NotificationUtil.getInstance()
        .show(this, Toast.Type.ERROR, LanguageUtil.getInstance().getString(langKey));
  }

  private void show(Toast.Type type, String message) {
    NotificationUtil.getInstance().show(this, type, message);
  }
}
