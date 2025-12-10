package com.vn.nghlong3004.client.controller.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.controller.ResetPasswordPresenter;
import com.vn.nghlong3004.client.controller.ResetPasswordView;
import com.vn.nghlong3004.client.controller.presenter.ResetPasswordPresenterImpl;
import com.vn.nghlong3004.client.controller.view.component.ButtonLink;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public class ResetPasswordPanel extends FormPanel implements ResetPasswordView {

  private JPasswordField txtPassword;
  private JPasswordField txtRePassword;
  private JButton cmdSubmit;

  private final ResetPasswordPresenter presenter;

  public ResetPasswordPanel(HttpService httpService, Gson gson) {
    this.presenter = new ResetPasswordPresenterImpl(this, httpService, gson);
    initUI();
  }

  private void initUI() {
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

    txtPassword = new JPasswordField();
    installRevealButton(txtPassword);
    txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getText("login_password"));
    add(txtPassword);

    JLabel lbRePassword = new JLabel(getText("reset_password_holder_place_re_password"));
    lbRePassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    add(lbRePassword, "gapy 10 n");

    txtRePassword = new JPasswordField();
    installRevealButton(txtRePassword);
    txtRePassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        getText("reset_password_holder_place_re_password_text"));
    add(txtRePassword);

    cmdSubmit = new ButtonLink("Submit");
    cmdSubmit.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
    add(cmdSubmit, "gapy 15 15");

    cmdSubmit.addActionListener(actionEvent -> presenter.handleSubmit());
  }

  private String getText(String key) {
    return LanguageUtil.getInstance().getString(key);
  }

  @Override
  public void showLoading(boolean isLoading) {
    SwingUtilities.invokeLater(() -> cmdSubmit.setEnabled(!isLoading));
  }

  @Override
  public void showSuccess(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.SUCCESS, getText(messageKey)));
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
  public void showInfo(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText(messageKey)));
  }

  @Override
  public void closeModal() {
    SwingUtilities.invokeLater(
        () -> ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId()));
  }

  @Override
  public String getPassword() {
    return new String(txtPassword.getPassword());
  }

  @Override
  public String getRePassword() {
    return new String(txtRePassword.getPassword());
  }
}
