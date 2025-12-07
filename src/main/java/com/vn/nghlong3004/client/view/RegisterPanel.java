package com.vn.nghlong3004.client.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/7/2025
 */
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

  private int gender;

  public RegisterPanel() {
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
    txtDateOfBirth.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "MM / DD / YYYY");
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

    panel.add(new JCheckBox("Đồng ý với điều khoản"), "gapy 2 2");

    cmdSignUp = new ButtonLink(LanguageUtil.getInstance().getString("register_button_register"));
    cmdSignUp.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
    panel.add(cmdSignUp);

    panel.add(new JSeparator(), "gapy 2 2");

    panel.add(
        new JLabel(LanguageUtil.getInstance().getString("register_login")), "split 2, gapx push n");

    cmdBackLogin = new ButtonLink(LanguageUtil.getInstance().getString("register_button_login"));
    panel.add(cmdBackLogin, "gapx n push");

    // event
    action();
    add(panel);
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
          String email = txtEmail.getText();
          String password = new String(txtPassword.getPassword());
          String rePassword = new String(txtRePassword.getPassword());
          String fullName = txtFullName.getText();
          NotificationUtil.getInstance()
              .show(
                  this,
                  Toast.Type.SUCCESS,
                  LanguageUtil.getInstance().getString("register_successfully"));
          ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId());
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
}
