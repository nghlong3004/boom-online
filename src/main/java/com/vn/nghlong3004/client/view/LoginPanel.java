package com.vn.nghlong3004.client.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.vn.nghlong3004.client.constant.ImageConstant;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.modal.Toast;
import raven.modal.component.DropShadowBorder;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/7/2025
 */
public class LoginPanel extends FormPanel {

  public LoginPanel() {
    init();
  }

  private void init() {
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

    JLabel lbTitle = new JLabel(LanguageUtil.getInstance().getString("login_title"));
    JLabel lbDescription = new JLabel(LanguageUtil.getInstance().getString("login_description"));
    lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +12;");

    loginContent.add(lbTitle);
    loginContent.add(lbDescription);

    JTextField txtUsername = new JTextField();
    JPasswordField txtPassword = new JPasswordField();
    JCheckBox chRememberMe = new JCheckBox(LanguageUtil.getInstance().getString("login_remember"));
    JButton cmdLogin = getCmdLogin();
    JButton cmdForgotPassword = getCmdButton("login_forgot_password");
    JButton cmdSignUp = getCmdButton("login_button_register");

    // style
    txtUsername.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        LanguageUtil.getInstance().getString("login_username"));
    txtPassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT,
        LanguageUtil.getInstance().getString("login_password"));

    panelLogin.putClientProperty(
        FlatClientProperties.STYLE, "[dark]background:tint($Panel.background,1%);");

    loginContent.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    txtUsername.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;" + "arc:12;");
    txtPassword.putClientProperty(
        FlatClientProperties.STYLE, "margin:4,10,4,10;" + "arc:12;" + "showRevealButton:true;");

    cmdLogin.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;" + "arc:12;");

    loginContent.add(new JLabel("Email"), "gapy 25");
    loginContent.add(txtUsername);

    loginContent.add(new JLabel("Password"), "gapy 10");
    loginContent.add(txtPassword);
    loginContent.add(chRememberMe, "split 2,gapy 10 10");
    loginContent.add(cmdForgotPassword, "gapx push n");
    loginContent.add(cmdLogin);
    loginContent.add(new JSeparator(), "gapy 5 2");
    loginContent.add(getCmdExternal());
    loginContent.add(
        new JLabel(LanguageUtil.getInstance().getString("login_register_description")),
        "split 2,gapx push n");
    loginContent.add(cmdSignUp, "gapx n push");

    panelLogin.add(loginContent);
    add(panelLogin);

    // event
    cmdLogin.addActionListener(
        e -> {
          String userName = txtUsername.getText();
          String password = String.valueOf(txtPassword.getPassword());
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, "Click login");
          txtUsername.setText("");
          txtPassword.setText("");
        });
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
    button.addActionListener(
        actionEven -> {
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, "Click Login with Google");
        });
    return button;
  }

  private ImageIcon getIcon(String path) {
    ImageIcon icon = new ImageIcon(path);
    Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImage);
  }

  private JButton getCmdLogin() {

    JButton cmdLogin =
        new JButton(LanguageUtil.getInstance().getString("login_button_login")) {
          @Override
          public boolean isDefaultButton() {
            return true;
          }
        };
    cmdLogin.setFocusPainted(false);
    cmdLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return cmdLogin;
  }

  private JButton getCmdButton(String key) {
    JButton cmd =
        new JButton(LanguageUtil.getInstance().getString(key)) {
          @Override
          public boolean isDefaultButton() {
            return true;
          }
        };
    cmd.setFocusPainted(false);
    cmd.setCursor(new Cursor(Cursor.HAND_CURSOR));
    cmd.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:15;"
            + "margin:1,5,1,5;"
            + "borderWidth:0;"
            + "focusWidth:0;"
            + "innerFocusWidth:0;"
            + "foreground:$Component.accentColor;"
            + "background:null;");
    return cmd;
  }

  private JPanel createInfo() {
    JPanel panelInfo = new JPanel(new MigLayout("wrap,al center", "[center]"));
    panelInfo.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    panelInfo.add(new JLabel("Don't remember your account details?"));
    panelInfo.add(new JLabel("Contact us at"), "split 2");
    LabelAdapter lbLink = new LabelAdapter("help@info.com");

    panelInfo.add(lbLink);

    // event
    lbLink.addOnClick(e -> {});

    return panelInfo;
  }

  private void applyShadowBorder(JPanel panel) {
    if (panel != null) {
      panel.setBorder(new DropShadowBorder(new Insets(5, 8, 12, 8), 1, 25));
    }
  }
}
