package vn.nghlong3004.boom.online.client.controller.view.room;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Image;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.model.room.PlayerSlot;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.util.ImageUtil;

/**
 * Project: boom-online-client
 *
 * <p>A single slot card (2x2 grid) inside Room.
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
public class PlayerSlotPanel extends JPanel {

  private final JLabel lblName;
  private final JLabel lblBadge;
  private final JLabel lblAvatar;
  private final JLabel lblCharacter;

  public PlayerSlotPanel() {
    setOpaque(false);
    setLayout(new MigLayout("fill, insets 14, wrap", "[grow,fill]", "[][grow][]"));

    putClientProperty(FlatClientProperties.STYLE, baseCardStyle(false, false, true));

    JPanel top = new JPanel(new MigLayout("insets 0, fillx", "[grow,fill][right]", "[]"));
    top.setOpaque(false);

    lblName = new JLabel(text("room.slot.empty"), SwingConstants.LEFT);
    lblName.putClientProperty(FlatClientProperties.STYLE, "font:bold +1;");

    lblBadge = badge(" ");
    lblBadge.setVisible(false);

    top.add(lblName);
    top.add(lblBadge);

    lblAvatar = new JLabel();
    lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);

    lblCharacter = new JLabel("", SwingConstants.CENTER);
    lblCharacter.putClientProperty(
        FlatClientProperties.STYLE, "foreground:$Label.disabledForeground; font: -1;");

    add(top, "growx");
    add(lblAvatar, "grow");
    add(lblCharacter, "growx");
  }

  public void setSlot(PlayerSlot slot, Long myId) {
    if (slot == null || !slot.isOccupied()) {
      lblName.setText(text("room.slot.empty"));
      lblCharacter.setText(" ");
      lblBadge.setVisible(false);
      lblAvatar.setIcon(null);

      putClientProperty(FlatClientProperties.STYLE, baseCardStyle(false, false, true));
      setEnabled(true);
      return;
    }

    boolean isMe = myId != null && myId.equals(slot.getUserId());
    boolean isHost = slot.isHost();
    boolean isReady = slot.isReady();
    boolean isBot = slot.isBot();

    String displayName = isBot ? text("room.slot.bot") : slot.getDisplayName();
    lblName.setText(displayName != null ? displayName : "");

    String badgeText;
    if (isHost) {
      badgeText = text("room.slot.host");
    } else {
      badgeText = isReady ? text("room.slot.ready") : text("room.slot.waiting");
    }
    lblBadge.setText(badgeText);
    lblBadge.setVisible(true);

    int charIdx = Math.floorMod(slot.getCharacterIndex(), RoomConstant.PLAYER_AVATARS.length);
    var img = ImageUtil.loadImage(RoomConstant.PLAYER_AVATARS[charIdx]);
    lblAvatar.setIcon(new ImageIcon(img.getScaledInstance(72, 69, Image.SCALE_SMOOTH)));

    putClientProperty(FlatClientProperties.STYLE, baseCardStyle(isMe, isReady || isHost, false));

    if (!isHost && !isReady) {
      putClientProperty(
          FlatClientProperties.STYLE,
          baseCardStyle(isMe, false, false) + "; background:fade($Panel.background,30%);");
    }
  }

  private static JLabel badge(String text) {
    JLabel b = new JLabel(text, SwingConstants.CENTER);
    b.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:999; font:bold -1; border:3,10,3,10;"
            + "background:fade($Component.accentColor,18%);"
            + "foreground:$Component.accentColor;");
    return b;
  }

  private static String baseCardStyle(boolean isMe, boolean isActive, boolean isEmpty) {
    String borderColor = isMe ? "$Component.accentColor" : "$Component.borderColor";
    String borderWidth = isMe ? "2" : "1";

    String bg = "background:fade($Panel.background,55%);";
    if (isEmpty) {
      bg = "background:fade($Panel.background,25%);";
    } else if (!isActive) {
      bg = "background:fade($Panel.background,40%);";
    }

    return "arc:20;"
        + "border:"
        + borderWidth
        + ","
        + borderWidth
        + ","
        + borderWidth
        + ","
        + borderWidth
        + ",fade("
        + borderColor
        + ",75%),,20;"
        + bg;
  }

  private static String safeName(String path) {
    if (path == null) return "";
    int slash = path.lastIndexOf('/');
    String file = slash >= 0 ? path.substring(slash + 1) : path;
    int dot = file.lastIndexOf('.');
    if (dot > 0) file = file.substring(0, dot);
    return file.replace("_avatar", "").replace('_', ' ');
  }

  private static String text(String key) {
    return I18NUtil.getString(key);
  }
}
