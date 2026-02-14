package vn.nghlong3004.boom.online.client.controller.view;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.DropShadowBorder;
import vn.nghlong3004.boom.online.client.controller.presenter.SettingPresenter;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.util.NotificationUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public class SettingPanel extends JPanel {

  private JCheckBox chkMusic;
  private JCheckBox chkSfx;
  private JSlider sliderVolume;
  private JButton cmdBack;
  private JButton cmdContinue;

  private final SettingPresenter presenter;

  public SettingPanel() {
    this.presenter = new SettingPresenter(this);

    setLayout(new MigLayout("al center center"));
    createAudioSettings();
    initEvents();
    initData();
  }

  public void initData() {
    presenter.loadInitialSettings();
  }

  private void createAudioSettings() {
    JPanel panelSettings =
        new JPanel(new BorderLayout()) {
          @Override
          public void updateUI() {
            super.updateUI();
            applyShadowBorder(this);
          }
        };
    panelSettings.setOpaque(false);
    applyShadowBorder(panelSettings);

    JPanel settingsContent =
        new JPanel(new MigLayout("fillx, wrap 2, insets 35 35 25 35", "[grow][fill, 150]"));

    JLabel lbTitle = new JLabel(getText("audio.title"));
    JLabel lbDescription = new JLabel(getText("audio.desc"));
    lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +12;");

    settingsContent.add(lbTitle, "span 2, align center");
    settingsContent.add(lbDescription, "span 2, align center, gapbottom 20");

    chkMusic = new JCheckBox("");
    chkSfx = new JCheckBox("");

    sliderVolume = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

    cmdBack = createButton("common.back", false);

    cmdContinue = createButton("common.continue", true);

    JPanel buttonPanel = new JPanel(new MigLayout("fillx, insets 0, gap 10", "[grow][fill, 150]"));
    buttonPanel.setOpaque(false);
    buttonPanel.add(cmdBack);
    buttonPanel.add(cmdContinue);

    panelSettings.putClientProperty(
        FlatClientProperties.STYLE, "[dark]background:tint($Panel.background,1%);");
    settingsContent.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    chkMusic.putClientProperty("FlatLaf.styleClass", "switch");
    chkSfx.putClientProperty("FlatLaf.styleClass", "switch");

    chkMusic.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
    chkSfx.putClientProperty(FlatClientProperties.STYLE, "font:bold;");

    settingsContent.add(new JLabel(getText("audio.label.music")), "gapy 10");
    settingsContent.add(chkMusic, "align right");

    settingsContent.add(new JLabel(getText("audio.label.sfx")), "gapy 10");
    settingsContent.add(chkSfx, "align right");

    settingsContent.add(new JLabel(getText("audio.label.master")), "gapy 10");
    settingsContent.add(sliderVolume);

    settingsContent.add(new JSeparator(), "span 2, gapy 20 10");

    settingsContent.add(buttonPanel, "span 2, align center");

    panelSettings.add(settingsContent);
    add(panelSettings);
  }

  private void initEvents() {
    sliderVolume.addChangeListener(
        e -> {
          if (!sliderVolume.getValueIsAdjusting()) {
            presenter.onVolumeChanged(sliderVolume.getValue());
          }
        });

    chkMusic.addActionListener(e -> presenter.onMusicToggled(chkMusic.isSelected()));
    chkSfx.addActionListener(e -> presenter.onSfxToggled(chkSfx.isSelected()));
    cmdBack.addActionListener(e -> presenter.onBackClicked());
    cmdContinue.addActionListener(e -> presenter.onContinueClicked());
  }

  public void showNotification(String messageKey, Toast.Type type) {
    NotificationUtil.getInstance().show(this, type, getText(messageKey));
  }

  public void closeSettingModal() {
    SwingUtilities.invokeLater(
        () -> ModalDialog.closeModal(ApplicationSession.getInstance().getHomeId()));
  }

  public void setMusicSelected(boolean selected) {
    chkMusic.setSelected(selected);
  }

  public void setSfxSelected(boolean selected) {
    chkSfx.setSelected(selected);
  }

  public void setVolumeValue(int volume) {
    sliderVolume.setValue(volume);
  }

  public void setContinueButtonVisible(boolean visible) {
    if (cmdContinue != null) {
      cmdContinue.setVisible(visible);
    }
  }

  public boolean isMusicSelected() {
    return chkMusic.isSelected();
  }

  public boolean isSfxSelected() {
    return chkSfx.isSelected();
  }

  public int getVolumeValue() {
    return sliderVolume.getValue();
  }

  private JButton createButton(String key, boolean isPrimary) {
    JButton cmd = new JButton(getText(key));
    cmd.setFocusPainted(false);
    cmd.setCursor(new Cursor(Cursor.HAND_CURSOR));

    String style = "margin:6,20,6,20; arc:12; font:bold;";
    if (isPrimary) {
      style += "background:$Component.accentColor;";
    } else {
      style += "background:$Button.background;";
    }

    cmd.putClientProperty(FlatClientProperties.STYLE, style);
    return cmd;
  }

  private void applyShadowBorder(JPanel panel) {
    if (panel != null) {
      panel.setBorder(new DropShadowBorder(new Insets(5, 8, 12, 8), 1, 25));
    }
  }

  private String getText(String key) {
    try {
      return I18NUtil.getString(key);
    } catch (Exception e) {
      return key;
    }
  }
}
