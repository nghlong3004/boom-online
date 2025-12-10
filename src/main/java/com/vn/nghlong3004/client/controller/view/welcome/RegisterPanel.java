package com.vn.nghlong3004.client.controller.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.controller.RegisterPresenter;
import com.vn.nghlong3004.client.controller.RegisterView;
import com.vn.nghlong3004.client.controller.presenter.RegisterPresenterImpl;
import com.vn.nghlong3004.client.controller.view.component.ButtonLink;
import com.vn.nghlong3004.client.service.HttpService;
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
public class RegisterPanel extends FormPanel implements RegisterView {

  private JTextField txtFullName;
  private JTextField txtEmail;
  private JTextField txtDateOfBirth;

  private JPasswordField txtPassword;
  private JPasswordField txtRePassword;

  private JButton cmdSignUp;
  private ButtonLink cmdBackLogin;

  private ButtonGroup groupPeople;
  private JRadioButton radioMale;
  private JRadioButton radioFemale;
  private JRadioButton radioDefault;

  private int gender = -1;

  private final RegisterPresenter presenter;

  public RegisterPanel(HttpService httpService, Gson gson) {
    this.presenter = new RegisterPresenterImpl(this, httpService, gson);

    setLayout(new MigLayout("al center center"));
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("insets n 2 n 2,fillx,wrap,width 300", "[fill,300]"));

    initialized();

    JTextArea text = new JTextArea(getText("register_title"));
    text.setEditable(false);
    text.setFocusable(false);
    text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
    panel.add(text);

    panel.add(new JSeparator(), "gapy 2 2");

    JLabel lbEmail = new JLabel("Email");
    lbEmail.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbEmail);

    txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getText("login_username"));
    panel.add(txtEmail);

    JLabel lbFullName = new JLabel(getText("register_full_name"));
    lbFullName.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbFullName);

    txtFullName.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("register_holder_place_full_name"));
    panel.add(txtFullName);

    JLabel lbPassword = new JLabel(getText("register_holder_place_password"));
    lbPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbPassword, "gapy 2 n");

    txtPassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("register_password"));
    panel.add(txtPassword);

    JLabel lbRePassword = new JLabel(getText("register_relay_password"));
    lbRePassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbRePassword, "gapy 2 n");

    installRevealButton(txtPassword);
    installRevealButton(txtRePassword);
    txtRePassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("register_holder_place_relay_password"));
    panel.add(txtRePassword);

    JLabel lbDateOfBirth = new JLabel(getText("register_birthday"));
    lbDateOfBirth.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbDateOfBirth, "gapy 2 n");

    txtDateOfBirth = new JTextField();
    txtDateOfBirth.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "DD / MM / YYYY");
    ((AbstractDocument) txtDateOfBirth.getDocument()).setDocumentFilter(getDocumentFilter());
    panel.add(txtDateOfBirth);

    JLabel lbNote = new JLabel(getText("register_note"));
    lbNote.putClientProperty(
        FlatClientProperties.STYLE, "font:-1;" + "foreground:$Label.disabledForeground;");
    panel.add(lbNote);

    JPanel panelGender = new JPanel(new MigLayout("", "fill, 61:70"));
    panelGender.putClientProperty(FlatClientProperties.STYLE, "background:null");

    groupPeople.add(radioMale);
    groupPeople.add(radioFemale);
    groupPeople.add(radioDefault);

    panelGender.add(new JLabel(getText("register_gender")));
    panelGender.add(radioMale);
    panelGender.add(radioFemale);
    panelGender.add(radioDefault, "wrap");
    panel.add(panelGender);

    cmdSignUp = new ButtonLink(getText("register_button_register"));
    cmdSignUp.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
    panel.add(cmdSignUp);

    panel.add(new JSeparator(), "gapy 2 2");

    panel.add(new JLabel(getText("register_login")), "split 2, gapx push n");

    cmdBackLogin = new ButtonLink(getText("register_button_login"));
    panel.add(cmdBackLogin, "gapx n push");
    add(panel);

    action();
  }

  private void initialized() {
    txtEmail = new JTextField();
    txtFullName = new JTextField();

    txtPassword = new JPasswordField();
    txtRePassword = new JPasswordField();

    groupPeople = new ButtonGroup();
    radioFemale = new JRadioButton(getText("register_gender_female"));
    radioMale = new JRadioButton(getText("register_gender_male"));
    radioDefault = new JRadioButton(getText("register_gender_other"));

    gender = -1;
  }

  private void action() {
    cmdBackLogin.addActionListener(
        actionEvent -> {
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText("login_button_login"));
          presenter.onLoginClicked();
        });

    cmdSignUp.addActionListener(
        actionEvent -> {
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText("handler"));
          presenter.handleRegister();
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

  private String getText(String key) {
    return LanguageUtil.getInstance().getString(key);
  }

  @Override
  public void showLoading(boolean isLoading) {
    SwingUtilities.invokeLater(() -> cmdSignUp.setEnabled(!isLoading));
  }

  @Override
  public void showSuccessMessage() {
    SwingUtilities.invokeLater(
        () ->
            NotificationUtil.getInstance()
                .show(this, Toast.Type.SUCCESS, getText("register_successfully")));
  }

  @Override
  public void showWarning(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.WARNING, getText(messageKey)));
  }

  @Override
  public void navigateToLogin() {
    SwingUtilities.invokeLater(
        () -> ModalDialog.popModel(ApplicationConfiguration.getInstance().getLoginId()));
  }

  @Override
  public void clearForm() {
    txtEmail.setText("");
    txtFullName.setText("");
    txtPassword.setText("");
    txtRePassword.setText("");
    txtDateOfBirth.setText("");
  }

  @Override
  public String getEmail() {
    return txtEmail.getText().trim();
  }

  @Override
  public String getFullName() {
    return txtFullName.getText().trim();
  }

  @Override
  public String getPassword() {
    return new String(txtPassword.getPassword());
  }

  @Override
  public String getRePassword() {
    return new String(txtRePassword.getPassword());
  }

  @Override
  public String getBirthday() {
    return txtDateOfBirth.getText().trim();
  }

  @Override
  public int getGender() {
    return this.gender;
  }
}
