package vn.nghlong3004.boom.online.client.controller.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import javax.swing.*;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import vn.nghlong3004.boom.online.client.controller.presenter.ForgotPasswordPresenter;
import vn.nghlong3004.boom.online.client.controller.view.CustomModalBorder;
import vn.nghlong3004.boom.online.client.controller.view.component.ButtonLink;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.util.NotificationUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/7/2025
 */
public class ForgotPasswordPanel extends FormPanel {

  private JTextField txtEmail;
  private JTextField txtOTP;
  private JButton cmdSubmit;
  private JButton btnGetOtp;

  @Setter private CustomModalBorder resetPasswordPanel;

  @Getter private final ForgotPasswordPresenter presenter;

  public ForgotPasswordPanel(HttpService httpService, Gson gson) {
    this.presenter = new ForgotPasswordPresenter(this, httpService, gson);

    initUI();
  }

  private void initUI() {
    setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));

    JTextArea text = new JTextArea(getText("forgot.desc"));
    text.setEditable(false);
    text.setFocusable(false);
    text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
    add(text);

    add(new JSeparator(), "gapy 15 15");

    JLabel lbEmail = new JLabel("Email");
    lbEmail.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbEmail);

    txtEmail = new JTextField();
    txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getText("login.input.email"));
    installGetOtpButton(txtEmail);
    add(txtEmail);

    JLabel lbOTP = new JLabel("OTP");
    lbOTP.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbOTP);

    txtOTP = new JTextField();
    txtOTP.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getText("forgot.label.otp"));
    add(txtOTP);

    cmdSubmit = new ButtonLink(getText("forgot.title"));
    cmdSubmit.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
    add(cmdSubmit, "gapy 15 15");

    ButtonLink cmdBackLogin = new ButtonLink(getText("forgot.btn.back_login"));
    add(cmdBackLogin, "grow 0,al center");

    cmdSubmit.setEnabled(false);
    txtOTP.setEnabled(false);

    cmdSubmit.addActionListener(e -> presenter.handleSubmit());
    cmdBackLogin.addActionListener(e -> presenter.onBackToLoginClicked());
  }

  private void installGetOtpButton(JTextField txt) {
    JToolBar toolBar = new JToolBar();
    toolBar.putClientProperty(FlatClientProperties.STYLE, "margin:0,0,0,5;");

    btnGetOtp = new JButton(getText("forgot.btn.send_otp"));
    btnGetOtp.addActionListener(e -> presenter.handleSendOtp());

    toolBar.add(btnGetOtp);
    txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
  }

  private String getText(String key) {
    return I18NUtil.getString(key);
  }

  public void showLoading(boolean isLoading) {}

  public void showSuccess(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.SUCCESS, getText(messageKey)));
  }

  public void showInfo(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText(messageKey)));
  }

  public void showWarning(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.WARNING, getText(messageKey)));
  }

  public void showError(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.ERROR, getText(messageKey)));
  }

  public void setOtpButtonText(String textOrKey) {
    SwingUtilities.invokeLater(
        () -> {
          String displayText = textOrKey;
          if (textOrKey.startsWith("forgot.")) {
            displayText = getText(textOrKey);
          }
          btnGetOtp.setText(displayText);
        });
  }

  public void setOtpButtonEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> btnGetOtp.setEnabled(enabled));
  }

  public void setSubmitEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> cmdSubmit.setEnabled(enabled));
  }

  public void setOtpFieldEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> txtOTP.setEnabled(enabled));
  }

  public void navigateToLogin() {
    SwingUtilities.invokeLater(
        () -> ModalDialog.popModel(ApplicationSession.getInstance().getWelcomeId()));
  }

  public void navigateToResetPassword() {
    SwingUtilities.invokeLater(
        () -> {
          if (resetPasswordPanel != null) {
            ModalDialog.pushModal(
                resetPasswordPanel, ApplicationSession.getInstance().getWelcomeId());
          }
        });
  }

  public String getEmail() {
    return txtEmail.getText();
  }

  public String getOtp() {
    return txtOTP.getText();
  }

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
