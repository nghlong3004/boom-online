package com.vn.nghlong3004.client.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/7/2025
 */
public class ForgotPasswordPanel extends FormPanel {

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

    JTextField txtEmail = new JTextField();
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
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, "Click Submit");
        });

    cmdBackLogin.addActionListener(
        actionEvent -> {
          NotificationUtil.getInstance()
              .show(
                  this,
                  Toast.Type.INFO,
                  LanguageUtil.getInstance().getString("login_button_login"));
          ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId());
        });
  }

  private void installRevealButton(JTextField txt, JTextField txtOTP, JButton cmdSubmit) {
    JToolBar toolBar = new JToolBar();
    toolBar.putClientProperty(FlatClientProperties.STYLE, "margin:0,0,0,5;");
    JButton button = new JButton("Gửi mã OTP");

    button.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent actionEvent) {}
        });
    toolBar.add(button);
    txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
  }

  private Timer timer;

  private class CreatePassword extends JPanel {

    public CreatePassword() {
      setLayout(new MigLayout("insets n 20 n 20,fillx,wrap,width 380", "[fill]"));
      JTextArea text = new JTextArea(LanguageUtil.getInstance().getString("register_password"));
      text.setEditable(false);
      text.setFocusable(false);
      text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
      add(text);

      add(new JSeparator(), "gapy 15 15");

      JLabel lbPassword =
          new JLabel(LanguageUtil.getInstance().getString("register_holder_place_password"));
      lbPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
      add(lbPassword, "gapy 10 n");
      JPasswordField txtPassword = new JPasswordField();
      installRevealButton(txtPassword);
      txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tối thiểu 6 kí tự");
      add(txtPassword);

      JLabel lbRePassword = new JLabel("Nhập lại mật khẩu");
      lbRePassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
      add(lbRePassword, "gapy 10 n");

      JPasswordField txtRePassword = new JPasswordField();
      installRevealButton(txtRePassword);
      txtRePassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập lại mật khẩu");
      add(txtRePassword);

      JButton cmdSubmit = new ButtonLink("Submit");
      cmdSubmit.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");

      add(cmdSubmit, "gapy 15 15");
      cmdSubmit.addActionListener(actionEvent -> {});
    }
  }
}
