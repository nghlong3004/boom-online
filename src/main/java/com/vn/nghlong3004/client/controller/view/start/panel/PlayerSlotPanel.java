package com.vn.nghlong3004.client.controller.view.start.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.vn.nghlong3004.client.application.character.CharacterAvatarGateway;
import com.vn.nghlong3004.client.domain.character.CharacterId;
import com.vn.nghlong3004.client.model.room.PlayerSlot;
import com.vn.nghlong3004.client.util.LanguageUtil;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public class PlayerSlotPanel extends JPanel {

  private static final int AVATAR_SIZE = 52;
  private static final int AVATAR_BOX = 56;

  private final CharacterAvatarGateway avatarGateway;

  private final String defaultPanelStyle;

  private final JLabel avatarLabel;
  private final JLabel nameLabel;
  private final JLabel characterLabel;
  private final JLabel statusLabel;

  public PlayerSlotPanel(CharacterAvatarGateway avatarGateway) {
    this.avatarGateway = avatarGateway;

    setLayout(new MigLayout("fill,insets 12", "[fill," + AVATAR_BOX + "!][grow,fill]", "[grow]"));
    setOpaque(true);

    defaultPanelStyle =
        "arc:16;"
            + "background:tint($Panel.background,1%);"
            + "border:8,8,8,8,fade($Component.accentColor,10%),,16;";
    putClientProperty(FlatClientProperties.STYLE, defaultPanelStyle);

    avatarLabel = new JLabel();
    avatarLabel.setOpaque(true);
    avatarLabel.setHorizontalAlignment(JLabel.CENTER);
    avatarLabel.setVerticalAlignment(JLabel.CENTER);
    avatarLabel.setPreferredSize(new Dimension(AVATAR_BOX, AVATAR_BOX));
    avatarLabel.putClientProperty(FlatClientProperties.STYLE, "arc:16; borderWidth:0;");
    avatarLabel.setBackground(UIManager.getColor("Panel.background"));

    nameLabel = new JLabel(text("room.slot.empty"));
    nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;");
    nameLabel.setMinimumSize(new Dimension(0, 0));

    characterLabel = new JLabel(" ");
    characterLabel.setOpaque(true);
    characterLabel.setMinimumSize(new Dimension(0, 0));
    characterLabel.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:999;"
            + "foreground:$Label.foreground;"
            + "background:tint($Panel.background,6%);"
            + "border:4,10,4,10,fade($Component.accentColor,0%),,999;"
            + "font:bold;"
            + "font:-1;");

    statusLabel = new JLabel(" ");
    statusLabel.setOpaque(true);
    statusLabel.setMinimumSize(new Dimension(0, 0));
    statusLabel.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:999;"
            + "background:fade($Component.accentColor,12%);"
            + "border:4,10,4,10,fade($Component.accentColor,0%),,999;"
            + "font:bold;"
            + "font:-1;");

    JPanel textPanel = new JPanel(new MigLayout("fill,insets 0,wrap 1", "[grow,fill]", "[]6[]6[]"));
    textPanel.putClientProperty(FlatClientProperties.STYLE, "background:null;");
    textPanel.setMinimumSize(new Dimension(0, 0));
    textPanel.add(nameLabel, "growx");
    textPanel.add(characterLabel, "growx");
    textPanel.add(statusLabel, "growx");

    add(avatarLabel, "aligny center");
    add(textPanel, "grow");

    characterLabel.setVisible(false);
    statusLabel.setVisible(false);
  }

  public void setSlot(PlayerSlot slot, CharacterId characterId) {
    if (slot == null || slot.isEmpty()) {
      putClientProperty(FlatClientProperties.STYLE, defaultPanelStyle);

      nameLabel.setText(text("room.slot.empty"));
      nameLabel.setToolTipText(null);
      nameLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
      nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;");

      characterLabel.setText(" ");
      characterLabel.setToolTipText(null);

      statusLabel.setText(" ");
      statusLabel.setForeground(UIManager.getColor("Label.disabledForeground"));

      avatarLabel.setIcon(null);
      avatarLabel.setBackground(UIManager.getColor("Panel.background"));

      characterLabel.setVisible(false);
      statusLabel.setVisible(false);
      return;
    }

    applyCharacterPanelBackground(characterId);

    nameLabel.setText(slot.getPlayerName());
    nameLabel.setToolTipText(slot.getPlayerName());
    nameLabel.setForeground(UIManager.getColor("Label.foreground"));
    nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;");

    characterLabel.setVisible(true);
    statusLabel.setVisible(true);

    String characterName = (characterId != null) ? characterId.name() : "";
    characterLabel.setText(String.format(text("room.slot.character_format"), characterName));
    characterLabel.setToolTipText(characterLabel.getText());

    if (avatarGateway != null && characterId != null) {
      ImageIcon icon = avatarGateway.getAvatar(characterId, AVATAR_SIZE);
      avatarLabel.setIcon(icon);
    } else {
      avatarLabel.setIcon(null);
    }

    avatarLabel.setBackground(resolveCharacterBackground(characterId));

    if (slot.isHost()) {
      nameLabel.setForeground(UIManager.getColor("Component.accentColor"));

      statusLabel.setText(text("room.slot.host"));
      statusLabel.setForeground(UIManager.getColor("Component.accentColor"));
      statusLabel.putClientProperty(
          FlatClientProperties.STYLE,
          "arc:999;"
              + "background:fade($Component.accentColor,16%);"
              + "border:4,10,4,10,fade($Component.accentColor,0%),,999;"
              + "font:bold;"
              + "font:-1;");
    } else {
      statusLabel.setText(text("room.slot.ready"));
      Color color =
          slot.isReady()
              ? UIManager.getColor("Component.accentColor")
              : UIManager.getColor("Label.disabledForeground");
      statusLabel.setForeground(color);
      statusLabel.putClientProperty(
          FlatClientProperties.STYLE,
          "arc:999;"
              + (slot.isReady()
                  ? "background:fade($Component.accentColor,12%);"
                  : "background:tint($Panel.background,6%);")
              + "border:4,10,4,10,fade($Component.accentColor,0%),,999;"
              + "font:bold;"
              + "font:-1;");
    }
  }

  private String text(String key) {
    return LanguageUtil.getInstance().getString(key);
  }

  private void applyCharacterPanelBackground(CharacterId characterId) {
    if (characterId == null) {
      putClientProperty(FlatClientProperties.STYLE, defaultPanelStyle);
      return;
    }

    String style =
        "arc:16;"
            + "background:$Character."
            + characterId.name()
            + ".background;"
            + "border:8,8,8,8,fade($Component.accentColor,10%),,16;";
    putClientProperty(FlatClientProperties.STYLE, style);
  }

  private Color resolveCharacterBackground(CharacterId characterId) {
    if (characterId == null) {
      return UIManager.getColor("Panel.background");
    }

    Color themed = UIManager.getColor("Character." + characterId.name() + ".background");
    return themed != null ? themed : UIManager.getColor("Panel.background");
  }
}
