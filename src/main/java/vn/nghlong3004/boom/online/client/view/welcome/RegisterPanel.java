package vn.nghlong3004.boom.online.client.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import vn.nghlong3004.boom.online.client.presenter.LoginPresenter;
import vn.nghlong3004.boom.online.client.presenter.RegisterPresenter;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.util.NotificationUtil;
import vn.nghlong3004.boom.online.client.view.component.ButtonLink;

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

  @Getter private int gender = -1;

  private final RegisterPresenter presenter;
  private final LoginPresenter loginPresenter;

  public RegisterPanel(HttpService httpService, Gson gson, LoginPresenter loginPresenter) {
    this.presenter = new RegisterPresenter(this, httpService, gson);
    this.loginPresenter = loginPresenter;
    setLayout(new MigLayout("al center center"));
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("insets n 2 n 2,fillx,wrap,width 400", "[fill,300]"));

    initialized();

    JTextArea text = new JTextArea(getText("register.title"));
    text.setEditable(false);
    text.setFocusable(false);
    text.putClientProperty(FlatClientProperties.STYLE, "border:0,0,0,0;" + "background:null;");
    panel.add(text);

    panel.add(new JSeparator(), "gapy 2 2");

    JLabel lbEmail = new JLabel("Email");
    lbEmail.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbEmail);

    txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getText("login.input.email"));
    panel.add(txtEmail);

    JLabel lbFullName = new JLabel(getText("register.label.fullname"));
    lbFullName.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbFullName);

    txtFullName.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("register.hint.fullname"));
    panel.add(txtFullName);

    JLabel lbPassword = new JLabel(getText("register.label.password"));
    lbPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbPassword, "gapy 2 n");

    txtPassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("register.hint.password"));
    panel.add(txtPassword);

    JLabel lbRePassword = new JLabel(getText("register.label.confirm_pass"));
    lbRePassword.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbRePassword, "gapy 2 n");

    installRevealButton(txtPassword);
    installRevealButton(txtRePassword);

    txtRePassword.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("register.hint.confirm_pass"));
    panel.add(txtRePassword);

    JLabel lbDateOfBirth = new JLabel(getText("register.label.dob"));
    lbDateOfBirth.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    panel.add(lbDateOfBirth, "gapy 2 n");

    txtDateOfBirth = new JTextField();
    txtDateOfBirth.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, getText("register.hint.dob"));
    ((AbstractDocument) txtDateOfBirth.getDocument()).setDocumentFilter(getDocumentFilter());
    panel.add(txtDateOfBirth);

    JLabel lbNote = new JLabel(getText("register.msg.gift_note"));
    lbNote.putClientProperty(
        FlatClientProperties.STYLE, "font:-1;" + "foreground:$Label.disabledForeground;");
    panel.add(lbNote);

    JPanel panelGender = new JPanel(new MigLayout("", "fill, 61:70"));
    panelGender.putClientProperty(FlatClientProperties.STYLE, "background:null");

    groupPeople.add(radioMale);
    groupPeople.add(radioFemale);
    groupPeople.add(radioDefault);

    panelGender.add(new JLabel(getText("register.label.gender")));
    panelGender.add(radioMale);
    panelGender.add(radioFemale);
    panelGender.add(radioDefault, "wrap");
    panel.add(panelGender);

    cmdSignUp = new ButtonLink(getText("register.btn.submit"));
    cmdSignUp.putClientProperty(FlatClientProperties.STYLE, "foreground:#FFFFFF;");
    panel.add(cmdSignUp);

    panel.add(new JSeparator(), "gapy 2 2");

    panel.add(new JLabel(getText("register.text.has_account")), "split 2, gapx push n");

    cmdBackLogin = new ButtonLink(getText("register.btn.login"));
    panel.add(cmdBackLogin, "gapx n push");
    add(panel);

    action();
  }

  public void setAccount() {
    loginPresenter.setAccount(getEmail(), getPassword());
  }

  private void initialized() {
    txtEmail = new JTextField();
    txtFullName = new JTextField();

    txtPassword = new JPasswordField();
    txtRePassword = new JPasswordField();

    groupPeople = new ButtonGroup();
    radioFemale = new JRadioButton(getText("register.gender.female"));
    radioMale = new JRadioButton(getText("register.gender.male"));
    radioDefault = new JRadioButton(getText("register.gender.other"));

    gender = -1;
  }

  private void action() {
    cmdBackLogin.addActionListener(
        actionEvent -> {
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText("login.btn.submit"));
          presenter.onLoginClicked();
        });

    cmdSignUp.addActionListener(
        actionEvent -> {
          NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText("common.processing"));
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
    return I18NUtil.getString(key);
  }

  public void showLoading(boolean isLoading) {
    SwingUtilities.invokeLater(() -> cmdSignUp.setEnabled(!isLoading));
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

  public void navigateToLogin() {
    SwingUtilities.invokeLater(
        () -> ModalDialog.popModel(ApplicationSession.getInstance().getWelcomeId()));
  }

  public void clearForm() {
    txtEmail.setText("");
    txtFullName.setText("");
    txtPassword.setText("");
    txtRePassword.setText("");
    txtDateOfBirth.setText("");
  }

  public String getEmail() {
    return txtEmail.getText().trim();
  }

  public String getFullName() {
    return txtFullName.getText().trim();
  }

  public String getPassword() {
    return new String(txtPassword.getPassword());
  }

  public String getRePassword() {
    return new String(txtRePassword.getPassword());
  }

  public String getBirthday() {
    return txtDateOfBirth.getText().trim();
  }
}
