package com.vn.nghlong3004.client.controller.presenter;

import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.context.GameContext;
import com.vn.nghlong3004.client.controller.RoomPresenter;
import com.vn.nghlong3004.client.controller.RoomView;
import com.vn.nghlong3004.client.model.room.ChatMessage;
import com.vn.nghlong3004.client.model.room.PlayerSlot;
import com.vn.nghlong3004.client.model.room.RoomMode;
import com.vn.nghlong3004.client.model.room.RoomState;
import com.vn.nghlong3004.client.service.RoomService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
@RequiredArgsConstructor
public class RoomPresenterImpl implements RoomPresenter {

  private final RoomView view;
  private final RoomService roomService;

  @Setter private String roomId;

  @Override
  public void loadRoom() {
    try {
      RoomState room = roomService.getRoom(roomId);
      view.render(room, isHost(room));
    } catch (Exception e) {
      view.showError(e.getMessage());
    }
  }

  @Override
  public void onBackToListClicked() {
    String local = getLocalName();
    try {
      RoomState room = roomService.getRoom(roomId);
      if (room.getMode() == RoomMode.OFFLINE) {
        GameContext.getInstance().previousState();
        return;
      }
      roomService.leaveRoom(roomId, local);
    } catch (Exception ignored) {

    }
    view.backToList();
  }

  @Override
  public void onToggleReadyClicked() {
    try {
      roomService.toggleReady(roomId, getLocalName());
      loadRoom();
    } catch (Exception e) {
      view.showError(e.getMessage());
    }
  }

  @Override
  public void onStartClicked() {
    try {
      RoomState room = roomService.getRoom(roomId);
      if (!isHost(room)) {
        view.showError(text("room.error.only_host_start"));
        return;
      }
      roomService.addChat(roomId, ChatMessage.system(text("room.chat.started")));
      loadRoom();
    } catch (Exception e) {
      view.showError(e.getMessage());
    }
  }

  @Override
  public void onCharacterPrevious() {
    changeCharacter(-1);
  }

  @Override
  public void onCharacterNext() {
    changeCharacter(1);
  }

  @Override
  public void onMapPrevious() {
    changeMap(-1);
  }

  @Override
  public void onMapNext() {
    changeMap(1);
  }

  @Override
  public void onSendChat(String message) {
    try {
      String local = getLocalName();
      roomService.addChat(roomId, ChatMessage.player(local, message, true));
      loadRoom();
    } catch (Exception e) {
      view.showError(e.getMessage());
    }
  }

  private void changeCharacter(int delta) {
    try {
      RoomState room = roomService.getRoom(roomId);
      PlayerSlot local = findLocalSlot(room);
      if (local == null) {
        return;
      }
      roomService.selectCharacter(roomId, getLocalName(), local.getCharacterIndex() + delta);
      loadRoom();
    } catch (Exception e) {
      view.showError(e.getMessage());
    }
  }

  private void changeMap(int delta) {
    try {
      RoomState room = roomService.getRoom(roomId);
      if (!isHost(room)) {
        return;
      }
      roomService.selectMap(roomId, room.getMapIndex() + delta);
      loadRoom();
    } catch (Exception e) {
      view.showError(e.getMessage());
    }
  }

  private boolean isHost(RoomState room) {
    PlayerSlot local = findLocalSlot(room);
    return local != null && local.isHost();
  }

  private PlayerSlot findLocalSlot(RoomState room) {
    String localName = getLocalName();
    return room.getSlots().stream()
        .filter(
            s -> !s.isEmpty() && !s.isMachine() && localName.equalsIgnoreCase(s.getPlayerName()))
        .findFirst()
        .orElse(null);
  }

  private String getLocalName() {
    String email = ApplicationContext.getInstance().getEmail();
    if (email != null && !email.isBlank()) {
      return email;
    }
    return "Guest";
  }

  private String text(String key) {
    return LanguageUtil.getInstance().getString(key);
  }
}
