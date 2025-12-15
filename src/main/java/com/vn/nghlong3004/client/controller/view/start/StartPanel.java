package com.vn.nghlong3004.client.controller.view.start;

import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.controller.view.start.panel.ListRoomPanel;
import com.vn.nghlong3004.client.controller.view.start.panel.RoomPanel;
import com.vn.nghlong3004.client.model.room.RoomMode;
import com.vn.nghlong3004.client.model.room.RoomState;
import com.vn.nghlong3004.client.service.RoomService;
import com.vn.nghlong3004.client.service.impl.InMemoryRoomServiceImpl;
import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public class StartPanel extends JPanel {

  private static final String CARD_LIST = "LIST";
  private static final String CARD_ROOM = "ROOM";

  private final CardLayout cardLayout;
  private final ListRoomPanel listRoomPanel;
  private final RoomPanel roomPanel;

  private final RoomService roomService;

  public StartPanel() {
    cardLayout = new CardLayout();
    setLayout(cardLayout);

    roomService = InMemoryRoomServiceImpl.getInstance();

    listRoomPanel = new ListRoomPanel(this::openRoom);
    roomPanel = new RoomPanel(this::backToList);

    add(listRoomPanel, CARD_LIST);
    add(roomPanel, CARD_ROOM);

    if (isOffline()) {
      RoomState roomState = roomService.createRoom(getLocalName(), RoomMode.OFFLINE);
      openRoom(roomState);
    } else {
      cardLayout.show(this, CARD_LIST);
      listRoomPanel.load();
    }
  }

  private void openRoom(com.vn.nghlong3004.client.model.room.RoomState roomState) {
    roomPanel.setRoom(roomState.getId());
    cardLayout.show(this, CARD_ROOM);
    roomPanel.load();
  }

  private void backToList() {
    cardLayout.show(this, CARD_LIST);
    listRoomPanel.load();
  }

  private boolean isOffline() {
    if (ApplicationContext.getInstance().isOfflineMode()) {
      return true;
    }
    String token = ApplicationContext.getInstance().getAccessToken();
    return token == null || token.isBlank();
  }

  private String getLocalName() {
    return ApplicationContext.getInstance().getName();
  }
}
