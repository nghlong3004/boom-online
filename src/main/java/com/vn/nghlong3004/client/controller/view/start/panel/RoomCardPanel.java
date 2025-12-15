package com.vn.nghlong3004.client.controller.view.start.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.vn.nghlong3004.client.model.room.RoomSummary;
import com.vn.nghlong3004.client.util.LanguageUtil;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public class RoomCardPanel extends JPanel {

  private RoomSummary room;

  private final JLabel nameLabel;
  private final JLabel infoLabel;

  public RoomCardPanel(Consumer<String> onDoubleClickJoin) {
    setLayout(new MigLayout("fill,insets 12", "[grow]", "[]5[]"));

    putClientProperty(
        FlatClientProperties.STYLE,
        "arc:16;"
            + "background:tint($Panel.background,1%);"
            + "border:8,8,8,8,fade($Component.accentColor,10%),,16;");

    nameLabel = new JLabel("-");
    nameLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +3;");
    infoLabel = new JLabel(" ");
    infoLabel.putClientProperty(
        FlatClientProperties.STYLE, "foreground:$Label.disabledForeground;");

    add(nameLabel, "growx,wrap");
    add(infoLabel, "growx");

    setCursor(new Cursor(Cursor.HAND_CURSOR));

    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (room == null) {
              return;
            }
            if (e.getClickCount() == 2 && onDoubleClickJoin != null) {
              onDoubleClickJoin.accept(room.id());
            }
          }
        });
  }

  public void setRoom(RoomSummary room) {
    this.room = room;
    nameLabel.setText(room.name());
    String modeText =
        switch (room.mode()) {
          case ONLINE -> text("room.mode.online");
          case OFFLINE -> text("room.mode.offline");
        };
    infoLabel.setText(modeText + " - " + room.currentPlayers() + "/" + room.maxPlayers());
  }

  public void clear() {
    this.room = null;
    nameLabel.setText("-");
    infoLabel.setText(" ");
  }

  private String text(String key) {
    return LanguageUtil.getInstance().getString(key);
  }
}
