package vn.nghlong3004.boom.online.client.controller.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import vn.nghlong3004.boom.online.client.controller.presenter.ForgotPasswordPresenter;
import vn.nghlong3004.boom.online.client.controller.presenter.ResetPasswordPresenter;
import vn.nghlong3004.boom.online.client.controller.view.component.ButtonLink;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.util.NotificationUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public class ResetPasswordPanel extends FormPanel {

  private JPasswordField txtPassword;
  private JPasswordField txtRePassword;
  private JButton cmdSubmit;

  private final ResetPasswordPresenter presenter;
  private final ForgotPasswordPresenter forgotPasswordPresenter;

  public ResetPasswordPanel(
      HttpService httpService, Gson gson, ForgotPasswordPresenter forgotPasswordPresenter) {
    this.presenter = new ResetPasswordPresenter(this, httpService, gson);
    this.forgotPasswordPresenter = forgotPasswordPresenter;
    initUI();
  }

  private void initUI() {
    setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));

    JTextArea text = new JTextArea(getText("reset.desc"));
    text.setEditable(false);
    text.setFocusable(false);
    text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
    add(text);

    add(new JSeparator(), "gapy 15 15");

    JLabel lbPassword = new JLabel(getText("reset.hint.new_pass"));
    lbPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbPassword, "gapy 10 n");

    txtPassword = new JPasswordField();
    installRevealButton(txtPassword);
    txtPassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("register.hint.password"));
    add(txtPassword);

    JLabel lbRePassword = new JLabel(getText("register.label.confirm_pass"));
    lbRePassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbRePassword, "gapy 10 n");

    txtRePassword = new JPasswordField();
    installRevealButton(txtRePassword);
    txtRePassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("reset.hint.confirm_pass"));
    add(txtRePassword);

    cmdSubmit = new ButtonLink(getText("reset.btn.confirm"));
    cmdSubmit.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
    add(cmdSubmit, "gapy 15 15");

    cmdSubmit.addActionListener(actionEvent -> presenter.handleSubmit());
  }

  private String getText(String key) {
    return I18NUtil.getString(key);
  }

  public void showLoading(boolean isLoading) {
    SwingUtilities.invokeLater(() -> cmdSubmit.setEnabled(!isLoading));
  }

  public void showSuccess(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.SUCCESS, getText(messageKey)));
  }

  public void showWarning(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.WARNING, getText(messageKey)));
  }

  public void showError(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.ERROR, getText(messageKey)));
  }

  public void showInfo(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText(messageKey)));
  }

  public void closeModal() {
    SwingUtilities.invokeLater(
        () -> {
          ModalDialog.popModel(ApplicationSession.getInstance().getWelcomeId());
          forgotPasswordPresenter.onBackToLoginClicked();
        });
  }

  public void clearForm() {
    txtPassword.setText("");
    txtRePassword.setText("");
  }

  public String getPassword() {
    return new String(txtPassword.getPassword());
  }

  public String getRePassword() {
    return new String(txtRePassword.getPassword());
  }
}
