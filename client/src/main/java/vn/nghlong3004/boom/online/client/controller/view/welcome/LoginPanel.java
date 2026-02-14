package vn.nghlong3004.boom.online.client.controller.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.DropShadowBorder;
import vn.nghlong3004.boom.online.client.constant.ImageConstant;
import vn.nghlong3004.boom.online.client.controller.presenter.LoginPresenter;
import vn.nghlong3004.boom.online.client.controller.view.CustomModalBorder;
import vn.nghlong3004.boom.online.client.controller.view.component.ButtonLink;
import vn.nghlong3004.boom.online.client.service.AuthService;
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
public class LoginPanel extends FormPanel {

  private JTextField txtUsername;
  private JPasswordField txtPassword;
  private JButton cmdLogin;

  @Getter private final LoginPresenter presenter;

  @Setter private CustomModalBorder registerPanel;
  @Setter private CustomModalBorder forgotPasswordPanel;

  public LoginPanel(HttpService httpService, AuthService authService, Gson gson) {
    this.presenter = new LoginPresenter(this, httpService, authService, gson);

    setLayout(new MigLayout("al center center"));
    createLogin();
  }

  private void createLogin() {
    JPanel panelLogin =
        new JPanel(new BorderLayout()) {
          @Override
          public void updateUI() {
            super.updateUI();
            applyShadowBorder(this);
          }
        };
    panelLogin.setOpaque(false);
    applyShadowBorder(panelLogin);

    JPanel loginContent = new JPanel(new MigLayout("fillx,wrap,insets 35 35 25 35", "[fill,300]"));

    JLabel lbTitle = new JLabel(getText("login.title"));
    JLabel lbDescription = new JLabel(getText("login.desc"));
    lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +12;");

    loginContent.add(lbTitle);
    loginContent.add(lbDescription);

    txtUsername = new JTextField();
    txtPassword = new JPasswordField();
    JCheckBox chRememberMe = new JCheckBox(getText("login.checkbox.remember"));
    cmdLogin = getCmdLogin();

    JButton cmdForgotPassword = new ButtonLink(getText("login.btn.forgot"));
    JButton cmdSignUp = new ButtonLink(getText("login.btn.register"));
    JButton cmdPlayingNow = new ButtonLink(getText("login.btn.offline"));

    txtUsername.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("login.input.email"));
    txtPassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("login.input.password"));

    panelLogin.putClientProperty(
        FlatClientProperties.STYLE, "[dark]background:tint($Panel.background,1%);");

    loginContent.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    txtUsername.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;" + "arc:12;");
    txtPassword.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;" + "arc:12;");
    installRevealButton(txtPassword);

    cmdLogin.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;" + "arc:12;");

    loginContent.add(new JLabel("Email"), "gapy 25");
    loginContent.add(txtUsername);

    loginContent.add(new JLabel(getText("register.label.password")), "gapy 10");
    loginContent.add(txtPassword);
    loginContent.add(chRememberMe, "split 2,gapy 10 10");
    loginContent.add(cmdForgotPassword, "gapx push n");
    loginContent.add(cmdLogin);
    loginContent.add(new JSeparator(), "gapy 5 2");
    loginContent.add(getCmdExternal());

    loginContent.add(new JLabel(getText("login.text.no_account")), "split 2,gapx push n");
    loginContent.add(cmdSignUp, "gapx n push");

    loginContent.add(new JLabel(getText("login.text.offline_hint")), "split 2,gapx push n");
    loginContent.add(cmdPlayingNow, "gapx n push");

    panelLogin.add(loginContent);
    add(panelLogin);

    cmdLogin.addActionListener(
        e -> {
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText("common.processing"));
          presenter.handleLogin();
        });
    txtPassword.addActionListener(e -> presenter.handleLogin());

    cmdSignUp.addActionListener(
        e -> {
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText("register.title"));
          presenter.onRegisterClicked();
        });

    cmdForgotPassword.addActionListener(
        e -> {
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText("forgot.title"));
          presenter.onForgotPasswordClicked();
        });

    cmdPlayingNow.addActionListener(
        e -> {
          presenter.onPlayingNowClicked();
        });
  }

  public void showLoading(boolean isLoading) {
    SwingUtilities.invokeLater(() -> cmdLogin.setEnabled(!isLoading));
  }

  public void showSuccess(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.SUCCESS, getText(messageKey)));
  }

  public void showError(String key) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.ERROR, getText(key)));
  }

  public void showInfo(String key) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText(key)));
  }

  public void closeLoginModal() {
    SwingUtilities.invokeLater(
        () -> ModalDialog.closeModal(ApplicationSession.getInstance().getWelcomeId()));
  }

  public void openRegisterModal() {
    if (registerPanel != null) {
      ModalDialog.pushModal(registerPanel, ApplicationSession.getInstance().getWelcomeId());
    }
  }

  public void openForgotPasswordModal() {
    if (forgotPasswordPanel != null) {
      ModalDialog.pushModal(forgotPasswordPanel, ApplicationSession.getInstance().getWelcomeId());
    }
  }

  public String getEmail() {
    return txtUsername.getText().trim();
  }

  public String getPassword() {
    return String.valueOf(txtPassword.getPassword());
  }

  public void clearForm() {
    txtUsername.setText("");
    txtPassword.setText("");
  }

  private Component getCmdExternal() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
    panel.putClientProperty(FlatClientProperties.STYLE, "background:null;");
    panel.add(cmdExternal());
    return panel;
  }

  private Component cmdExternal() {
    JButton button = new JButton(getIcon("src/main/resources" + ImageConstant.BUTTON_GOOGLE));
    button.setFocusPainted(false);
    button.putClientProperty(
        FlatClientProperties.STYLE,
        "[light]foreground:darken(@foreground, 80%);"
            + "[dark]foreground:lighten(@foreground, 60%);"
            + "[light]background:lighten(@background, 80%);"
            + "[dark]background:lighten(@background, 60%);");
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    button.addActionListener(actionEven -> presenter.onGoogleLoginClicked());

    return button;
  }

  private ImageIcon getIcon(String path) {
    ImageIcon icon = new ImageIcon(path);
    Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImage);
  }

  private JButton getCmdLogin() {
    JButton cmdLogin =
        new JButton(getText("login.btn.submit")) {
          @Override
          public boolean isDefaultButton() {
            return true;
          }
        };
    cmdLogin.setFocusPainted(false);
    cmdLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return cmdLogin;
  }

  private void applyShadowBorder(JPanel panel) {
    if (panel != null) {
      panel.setBorder(new DropShadowBorder(new Insets(5, 8, 12, 8), 1, 25));
    }
  }

  private String getText(String key) {
    return I18NUtil.getString(key);
  }

  public void setPassword(String message) {
    if (message != null) {
      this.txtPassword.setText(message);
    }
  }

  public void setEmail(String message) {
    if (message != null) {
      this.txtUsername.setText(message);
    }
  }
}
