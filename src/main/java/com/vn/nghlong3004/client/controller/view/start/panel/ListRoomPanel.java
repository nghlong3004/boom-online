package com.vn.nghlong3004.client.controller.view.start.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.vn.nghlong3004.client.constant.ImageConstant;
import com.vn.nghlong3004.client.controller.ListRoomView;
import com.vn.nghlong3004.client.controller.presenter.ListRoomPresenterImpl;
import com.vn.nghlong3004.client.controller.view.component.StartButton;
import com.vn.nghlong3004.client.model.room.RoomState;
import com.vn.nghlong3004.client.model.room.RoomSummary;
import com.vn.nghlong3004.client.service.RoomService;
import com.vn.nghlong3004.client.service.impl.InMemoryRoomServiceImpl;
import com.vn.nghlong3004.client.util.ImageUtil;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import raven.modal.Toast;
import raven.modal.component.DropShadowBorder;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public class ListRoomPanel extends JPanel implements ListRoomView {

  private final Consumer<RoomState> openRoom;

  private final ListRoomPresenterImpl presenter;

  private final List<RoomCardPanel> roomCards;

  private final JLabel pageLabel;
  private final JButton prevButton;
  private final JButton nextButton;

  public ListRoomPanel(Consumer<RoomState> openRoom) {
    this.openRoom = openRoom;
    roomCards = new ArrayList<>(3);
    RoomService roomService = InMemoryRoomServiceImpl.getInstance();
    presenter = new ListRoomPresenterImpl(this, roomService);

    setLayout(new MigLayout("fill,insets 24", "[grow]", "[grow]"));
    putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JPanel card =
        new JPanel(new BorderLayout()) {
          @Override
          public void updateUI() {
            super.updateUI();
            applyShadowBorder(this);
          }
        };
    card.setOpaque(false);
    applyShadowBorder(card);

    JPanel content = new JPanel(new MigLayout("fill,insets 22", "[grow]", "[][grow][]"));
    content.putClientProperty(
        FlatClientProperties.STYLE, "[dark]background:tint($Panel.background,1%);" + "arc:18;");

    JButton backButton = createIconTextButton(text("common.back"));
    JButton createButton = new StartButton(text("listroom.create"), true);
    JLabel title = new JLabel(text("listroom.title"));
    title.putClientProperty(FlatClientProperties.STYLE, "font:bold +6;");

    JPanel header = new JPanel(new MigLayout("fill,insets 0", "[left][grow][right]", "[]"));
    header.putClientProperty(FlatClientProperties.STYLE, "background:null;");
    header.add(backButton, "w 120!");
    header.add(title, "align center");
    header.add(createButton, "w 160!");

    content.add(header, "growx,wrap 14");

    JPanel list = new JPanel(new MigLayout("fill,wrap,insets 0", "[grow]", "[]14[]14[]"));
    list.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    for (int i = 0; i < 3; i++) {
      RoomCardPanel newCard = new RoomCardPanel(presenter::onJoinRoomDoubleClicked);
      roomCards.add(newCard);
      list.add(newCard, "growx,h 90!");
    }

    content.add(list, "grow,wrap 14");

    prevButton = new StartButton("<", false);
    nextButton = new StartButton(">", false);
    pageLabel = new JLabel(String.format(text("listroom.page_format"), 1, 1));
    pageLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold;");

    JPanel footer = new JPanel(new MigLayout("insets 0", "[left][grow][right]", "[]"));
    footer.putClientProperty(FlatClientProperties.STYLE, "background:null;");
    footer.add(prevButton, "w 50!");
    footer.add(pageLabel, "align center");
    footer.add(nextButton, "w 50!");

    content.add(footer, "growx");

    card.add(content, BorderLayout.CENTER);
    add(card, "grow");

    backButton.addActionListener(e -> presenter.onBackClicked());
    createButton.addActionListener(e -> presenter.onCreateRoomClicked());
    prevButton.addActionListener(e -> presenter.previousPage());
    nextButton.addActionListener(e -> presenter.nextPage());
  }

  public void load() {
    presenter.loadRooms();
  }

  @Override
  public void showRooms(List<RoomSummary> rooms, int pageIndex, int totalPages) {
    for (int i = 0; i < roomCards.size(); i++) {
      RoomCardPanel card = roomCards.get(i);
      if (i < rooms.size()) {
        card.setRoom(rooms.get(i));
        card.setVisible(true);
      } else {
        card.clear();
        card.setVisible(false);
      }
    }

    int shownTotal = Math.max(totalPages, 1);
    int shownPage = Math.min(pageIndex + 1, shownTotal);
    pageLabel.setText(String.format(text("listroom.page_format"), shownPage, shownTotal));

    prevButton.setEnabled(pageIndex > 0);
    nextButton.setEnabled(pageIndex < totalPages - 1);

    revalidate();
    repaint();
  }

  @Override
  public void openRoom(RoomState roomState) {
    if (openRoom != null && roomState != null) {
      openRoom.accept(roomState);
    }
  }

  @Override
  public void showError(String message) {
    NotificationUtil.getInstance().show(this, Toast.Type.ERROR, message);
  }

  private JButton createIconTextButton(String text) {
    JButton button = new StartButton(text, false);
    try {
      Image img =
          ImageUtil.loadImage(ImageConstant.BACK)
              .getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
      button.setIcon(new javax.swing.ImageIcon(img));
    } catch (Exception ignored) {

    }
    button.putClientProperty(
        FlatClientProperties.STYLE,
        button.getClientProperty(FlatClientProperties.STYLE) + ";iconTextGap:8;");
    return button;
  }

  private void applyShadowBorder(JPanel panel) {
    if (panel != null) {
      panel.setBorder(new DropShadowBorder(new Insets(8, 10, 16, 10), 1, 25));
    }
  }

  private String text(String key) {
    return LanguageUtil.getInstance().getString(key);
  }
}
