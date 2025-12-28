package vn.nghlong3004.boom.online.client.controller.presenter;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.controller.view.room.RoomPanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.UserSession;
import vn.nghlong3004.boom.online.client.util.DebouncerUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Slf4j
@RequiredArgsConstructor
public class RoomPresenter {

  private final RoomPanel view;
  private final RoomService roomService;
  private final WebSocketService webSocketService;

  private Room currentRoom;
  private boolean isProcessing = false;

  public void update(Room room) {
    this.currentRoom = room;
    this.roomService.setCurrentRoom(room);
    view.renderRoom(currentRoom);
  }

  private void executeAction(CompletableFuture<Room> action) {
    if (isProcessing) return;

    isProcessing = true;
    view.setControlsEnabled(false);

    action
            .thenAccept(
                    room ->
                            SwingUtilities.invokeLater(
                                    () -> {
                                      update(room);
                                      unlock();
                                    }))
            .exceptionally(
                    ex -> {
                      unlock();
                      return null;
                    });
  }

  private void executeOptimisticAction(CompletableFuture<Room> action, Runnable localUpdate) {
    SwingUtilities.invokeLater(localUpdate);
    action.exceptionally(ex -> {
      log.error("Action failed", ex);
      return null;
    });
  }

  private void unlock() {
    isProcessing = false;
    view.setControlsEnabled(true);
  }

  public void onMapLeft() {
    int next = Math.floorMod(currentRoom.getMapIndex() - 1, RoomConstant.MAP_AVATARS.length);
    DebouncerUtil.debounce("CHANGE_MAP", 200, () -> {
      executeOptimisticAction(roomService.changeMap(next), () -> {
        currentRoom.setMapIndex(next);
        view.renderRoom(currentRoom);
      });
    });
  }

  public void onMapRight() {
    int next = Math.floorMod(currentRoom.getMapIndex() + 1, RoomConstant.MAP_AVATARS.length);
    DebouncerUtil.debounce("CHANGE_MAP", 200, () -> {
      executeOptimisticAction(roomService.changeMap(next), () -> {
        currentRoom.setMapIndex(next);
        view.renderRoom(currentRoom);
      });
    });
  }

  public void onCharacterLeft() {
    changeCharacterBy(-1);
  }

  public void onCharacterRight() {
    changeCharacterBy(1);
  }

  private void changeCharacterBy(int delta) {
    Long myId = UserSession.getInstance().getCurrentUser().getId();
    int myIndex = view.getMyCharacterIndex(currentRoom, myId);
    int next = Math.floorMod(myIndex + delta, RoomConstant.PLAYER_AVATARS.length);

    executeOptimisticAction(roomService.changeCharacter(next), () -> {
      currentRoom.getSlots().stream()
              .filter(s -> s != null && myId.equals(s.getUserId()))
              .findFirst()
              .ifPresent(s -> s.setCharacterIndex(next));
      view.renderRoom(currentRoom);
    });
  }

  public void onSendChat(String content) {
    if (content.isBlank()) return;
    DebouncerUtil.debounce("CHAT", 500, () -> executeAction(roomService.sendChat(content)));
  }

  public void onBackClicked() {
    if (!ApplicationSession.getInstance().isOfflineMode()) {
      webSocketService.disconnect();
      view.backToLobby();
    } else {
      view.backToHome();
    }
  }

  public void onStartClicked() {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentRoom != null && !Objects.equals(currentRoom.getOwnerId(), currentUser.getId())) {
      return;
    }
    roomService.startGame().exceptionally(ex -> {
      log.error("Failed to start game", ex);
      return null;
    });
  }

  public void onToggleReadyClicked() {
    executeAction(roomService.toggleReady());
  }
}