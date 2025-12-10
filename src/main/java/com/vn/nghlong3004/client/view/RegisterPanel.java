package com.vn.nghlong3004.client.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.model.request.RegisterRequest;
import com.vn.nghlong3004.client.model.response.ErrorResponse;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
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
public class RegisterPanel extends FormPanel {

  private JTextField txtFullName;
  private JTextField txtEmail;
  private final JTextField txtDateOfBirth;

  private JPasswordField txtPassword;
  private JPasswordField txtRePassword;

  private final JButton cmdSignUp;
  private final ButtonLink cmdBackLogin;

  private ButtonGroup groupPeople;

  private JRadioButton radioMale;
  private JRadioButton radioFemale;
  private JRadioButton radioDefault;

  private int gender = -1;

  public RegisterPanel(HttpService httpService, Gson gson) {
    super(httpService, gson);
    setLayout(new MigLayout("al center center"));
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("insets n 2 n 2,fillx,wrap,width 300", "[fill,300]"));
    initialized();
    JTextArea text = new JTextArea(LanguageUtil.getInstance().getString("register_title"));
    text.setEditable(false);
    text.setFocusable(false);
    text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
    panel.add(text);

    panel.add(new JSeparator(), "gapy 2 2");

    JLabel lbEmail = new JLabel("Email");
    lbEmail.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbEmail);

    txtEmail.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        LanguageUtil.getInstance().getString("login_username"));
    panel.add(txtEmail);

    JLabel lbFullName = new JLabel(LanguageUtil.getInstance().getString("register_full_name"));
    lbFullName.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbFullName);

    txtFullName.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        LanguageUtil.getInstance().getString("register_holder_place_full_name"));
    panel.add(txtFullName);

    JLabel lbPassword =
        new JLabel(LanguageUtil.getInstance().getString("register_holder_place_password"));
    lbPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbPassword, "gapy 2 n");

    txtPassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        LanguageUtil.getInstance().getString("register_password"));
    panel.add(txtPassword);

    JLabel lbRePassword =
        new JLabel(LanguageUtil.getInstance().getString("register_relay_password"));
    lbRePassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbRePassword, "gapy 2 n");

    installRevealButton(txtPassword);
    installRevealButton(txtRePassword);
    txtRePassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        LanguageUtil.getInstance().getString("register_holder_place_relay_password"));
    panel.add(txtRePassword);

    JLabel lbDateOfBirth = new JLabel(LanguageUtil.getInstance().getString("register_birthday"));
    lbDateOfBirth.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbDateOfBirth, "gapy 2 n");

    txtDateOfBirth = new JTextField();
    txtDateOfBirth.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "DD / MM / YYYY");
    ((AbstractDocument) txtDateOfBirth.getDocument()).setDocumentFilter(getDocumentFilter());
    panel.add(txtDateOfBirth);

    JLabel lbNote = new JLabel(LanguageUtil.getInstance().getString("register_note"));
    lbNote.putClientProperty(
        FlatClientProperties.STYLE, "font:-1;" + "foreground:$Label.disabledForeground;");
    panel.add(lbNote);

    JPanel panelGender = new JPanel(new MigLayout("", "fill, 61:70"));
    panelGender.putClientProperty(FlatClientProperties.STYLE, "background:null");

    groupPeople.add(radioMale);
    groupPeople.add(radioFemale);
    groupPeople.add(radioDefault);

    panelGender.add(new JLabel(LanguageUtil.getInstance().getString("register_gender")));
    panelGender.add(radioMale);
    panelGender.add(radioFemale);
    panelGender.add(radioDefault, "wrap");
    panel.add(panelGender);

    cmdSignUp = new ButtonLink(LanguageUtil.getInstance().getString("register_button_register"));
    cmdSignUp.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
    panel.add(cmdSignUp);

    panel.add(new JSeparator(), "gapy 2 2");

    panel.add(
        new JLabel(LanguageUtil.getInstance().getString("register_login")), "split 2, gapx push n");

    cmdBackLogin = new ButtonLink(LanguageUtil.getInstance().getString("register_button_login"));
    panel.add(cmdBackLogin, "gapx n push");
    add(panel);
    // event
    action();
  }

  private DocumentFilter getDocumentFilter() {
    return new DocumentFilter() {
      final int maxCharacters = 10;

      @Override
      public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
          throws BadLocationException {
        if (!text.matches("[0-9]*")) {
          return;
        }

        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        StringBuilder sb = new StringBuilder(currentText);
        sb.replace(offset, offset + length, text);

        if (sb.length() > maxCharacters) {
          Toolkit.getDefaultToolkit().beep();
          return;
        }

        if (!text.isEmpty() && (sb.length() == 2 || sb.length() == 5)) {
          sb.append("/");
          super.replace(fb, 0, fb.getDocument().getLength(), sb.toString(), attrs);
        } else {
          super.replace(fb, offset, length, text, attrs);
        }
      }

      @Override
      public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
          throws BadLocationException {
        replace(fb, offset, 0, string, attr);
      }
    };
  }

  private void action() {
    cmdBackLogin.addActionListener(
        actionEvent -> {
          NotificationUtil.getInstance()
              .show(
                  this,
                  Toast.Type.INFO,
                  LanguageUtil.getInstance().getString("login_button_login"));
          ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId());
        });

    cmdSignUp.addActionListener(
        actionEvent -> {
          NotificationUtil.getInstance()
              .show(this, Toast.Type.INFO, LanguageUtil.getInstance().getString("handler"));
          String email = txtEmail.getText().trim();
          String password = new String(txtPassword.getPassword());
          String rePassword = new String(txtRePassword.getPassword());
          String birthday = txtDateOfBirth.getText().trim();
          String fullName = txtFullName.getText().trim();

          if (!validateInput(email, password, rePassword, fullName, birthday)) {
            return;
          }

          handleRegister(email, password, fullName, birthday);
        });

    ItemListener itemListener =
        e -> {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            JRadioButton selected = (JRadioButton) e.getItem();
            if (selected.equals(radioMale)) {
              gender = 0;
            } else if (selected.equals(radioFemale)) {
              gender = 1;
            } else if (selected.equals(radioDefault)) {
              gender = 2;
            }
          }
        };
    radioMale.addItemListener(itemListener);
    radioFemale.addItemListener(itemListener);
    radioDefault.addItemListener(itemListener);
  }

  private void handleRegister(String email, String password, String fullName, String birthday) {
    cmdSignUp.setEnabled(false);

    RegisterRequest request = new RegisterRequest(email, password, birthday, fullName, gender);

    httpService
        .sendRegisterRequest(request)
        .thenAccept(response -> SwingUtilities.invokeLater(this::onRegisterSuccess))
        .exceptionally(
            ex -> {
              Throwable cause = ex.getCause();
              String rawMessage = (cause != null) ? cause.getMessage() : ex.getMessage();
              String messageKey = mapErrorToMessageKey(rawMessage);
              SwingUtilities.invokeLater(
                  () -> {
                    showWarning(messageKey);
                    cmdSignUp.setEnabled(true);
                  });
              return null;
            });
  }

  private void onRegisterSuccess() {
    cmdSignUp.setEnabled(true);
    clearRegisterForm();

    NotificationUtil.getInstance()
        .show(
            this,
            Toast.Type.SUCCESS,
            LanguageUtil.getInstance().getString("register_successfully"));

    ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId());
  }

  private void clearRegisterForm() {
    txtEmail.setText("");
    txtFullName.setText("");
    txtPassword.setText("");
    txtRePassword.setText("");
    txtDateOfBirth.setText("");
  }

  private String mapErrorToMessageKey(String errorBody) {
    if (errorBody == null) return "server_error";
    if (errorBody.contains("ConnectException") || errorBody.contains("Network is unreachable")) {
      return "server_error";
    }

    try {
      ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);
      if (errorResponse != null && errorResponse.code() != null) {
        return switch (errorResponse.code()) {
          case "EmailAlready" -> "register_email_exists";
          case "InvalidRequest" -> "register_empty_fields";
          case "InternalError" -> "server_internal";
          default -> "register_failed";
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
      return "unknown_error";
    }

    return "register_failed";
  }

  private void initialized() {
    txtEmail = new JTextField();
    txtFullName = new JTextField();

    txtPassword = new JPasswordField();
    txtRePassword = new JPasswordField();

    groupPeople = new ButtonGroup();
    radioFemale = new JRadioButton(LanguageUtil.getInstance().getString("register_gender_female"));
    radioMale = new JRadioButton(LanguageUtil.getInstance().getString("register_gender_male"));
    radioDefault = new JRadioButton(LanguageUtil.getInstance().getString("register_gender_other"));

    gender = -1;
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

  private void showWarning(String languageKey) {
    NotificationUtil.getInstance()
        .show(this, Toast.Type.WARNING, LanguageUtil.getInstance().getString(languageKey));
  }

  private boolean validateInput(
      String email, String pass, String rePass, String fullName, String birthday) {
    if (email.isBlank()
        || pass.isBlank()
        || rePass.isBlank()
        || fullName.isBlank()
        || birthday.isBlank()) {
      showWarning("register_empty_fields");
      return false;
    }

    if (gender == -1) {
      showWarning("register_empty_fields");
      return false;
    }

    if (!isValidEmail(email)) {
      showWarning("invalid_email");
      return false;
    }

    if (!isValidDate(birthday)) {
      showWarning("register_invalid_birthday");
      return false;
    }

    if (pass.length() < 6) {
      showWarning("password_short");
      return false;
    }

    if (!pass.equals(rePass)) {
      showWarning("register_match_password");
      return false;
    }

    return true;
  }
}
