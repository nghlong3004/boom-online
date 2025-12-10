package com.vn.nghlong3004.client.controller.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.controller.ForgotPasswordPresenter;
import com.vn.nghlong3004.client.controller.ForgotPasswordView;
import com.vn.nghlong3004.client.controller.presenter.ForgotPasswordPresenterImpl;
import com.vn.nghlong3004.client.controller.view.CustomModalBorder;
import com.vn.nghlong3004.client.controller.view.component.ButtonLink;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
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
public class ForgotPasswordPanel extends FormPanel implements ForgotPasswordView {

  private JTextField txtEmail;
  private JTextField txtOTP;
  private JButton cmdSubmit;
  private JButton btnGetOtp;

  @Setter private CustomModalBorder resetPasswordPanel;

  private final ForgotPasswordPresenter presenter;

  public ForgotPasswordPanel(HttpService httpService, Gson gson) {
    super(httpService, gson);
    this.presenter = new ForgotPasswordPresenterImpl(this, httpService, gson);

    initUI();
  }

  private void initUI() {
    setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));

    JTextArea text = new JTextArea(getText("forgot_password_description"));
    text.setEditable(false);
    text.setFocusable(false);
    text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
    add(text);

    add(new JSeparator(), "gapy 15 15");

    JLabel lbEmail = new JLabel("Email");
    lbEmail.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbEmail);

    txtEmail = new JTextField();
    txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getText("login_username"));
    installGetOtpButton(txtEmail);
    add(txtEmail);

    JLabel lbOTP = new JLabel("OTP");
    lbOTP.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbOTP);

    txtOTP = new JTextField();
    txtOTP.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("forgot_password_title_otp"));
    add(txtOTP);

    cmdSubmit = new ButtonLink("Submit");
    cmdSubmit.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
    add(cmdSubmit, "gapy 15 15");

    ButtonLink cmdBackLogin = new ButtonLink(getText("forgot_password_login"));
    add(cmdBackLogin, "grow 0,al center");

    cmdSubmit.setEnabled(false);
    txtOTP.setEnabled(false);

    cmdSubmit.addActionListener(e -> presenter.handleSubmit());
    cmdBackLogin.addActionListener(e -> presenter.onBackToLoginClicked());
  }

  private void installGetOtpButton(JTextField txt) {
    JToolBar toolBar = new JToolBar();
    toolBar.putClientProperty(FlatClientProperties.STYLE, "margin:0,0,0,5;");

    btnGetOtp = new JButton(getText("forgot_password_btn_otp"));
    btnGetOtp.addActionListener(e -> presenter.handleSendOtp());

    toolBar.add(btnGetOtp);
    txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
  }

  private String getText(String key) {
    return LanguageUtil.getInstance().getString(key);
  }

  @Override
  public void showLoading(boolean isLoading) {}

  @Override
  public void showSuccess(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.SUCCESS, getText(messageKey)));
  }

  @Override
  public void showInfo(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText(messageKey)));
  }

  @Override
  public void showWarning(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.WARNING, getText(messageKey)));
  }

  @Override
  public void showError(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.ERROR, getText(messageKey)));
  }

  @Override
  public void setOtpButtonText(String textOrKey) {
    SwingUtilities.invokeLater(
        () -> {
          String displayText = textOrKey;
          if (textOrKey.startsWith("forgot_password_")) {
            displayText = getText(textOrKey);
          }
          btnGetOtp.setText(displayText);
        });
  }

  @Override
  public void setOtpButtonEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> btnGetOtp.setEnabled(enabled));
  }

  @Override
  public void setSubmitEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> cmdSubmit.setEnabled(enabled));
  }

  @Override
  public void setOtpFieldEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> txtOTP.setEnabled(enabled));
  }

  @Override
  public void navigateToLogin() {
    SwingUtilities.invokeLater(
        () -> ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId()));
  }

  @Override
  public void navigateToResetPassword() {
    SwingUtilities.invokeLater(
        () -> {
          if (resetPasswordPanel != null) {
            ModalDialog.pushModal(
                resetPasswordPanel, ApplicationConfiguration.getInstance().getLoginId());
          }
        });
  }

  @Override
  public String getEmail() {
    return txtEmail.getText();
  }

  @Override
  public String getOtp() {
    return txtOTP.getText();
  }

  @Override
  public void clearForm() {
    SwingUtilities.invokeLater(
        () -> {
          txtEmail.setText("");
          txtOTP.setText("");
          cmdSubmit.setEnabled(false);
          txtOTP.setEnabled(false);
        });
  }

  @Override
  public void removeNotify() {
    presenter.cleanup();
    super.removeNotify();
  }
}
