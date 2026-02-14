package vn.nghlong3004.boom.online.client.controller.presenter;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.controller.view.room.RoomPanel;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.core.state.GameStateType;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.model.room.RoomStatus;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.PlayingSession;
import vn.nghlong3004.boom.online.client.session.UserSession;

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

  public void update(Room room, boolean isLock) {
    SwingUtilities.invokeLater(
        () -> {
          this.currentRoom = room;
          this.roomService.setCurrentRoom(room);
          view.renderRoom(currentRoom);
          unlock(isLock);
          if (room.getStatus() == RoomStatus.PLAYING) {
            PlayingSession.getInstance().initFromRoom(currentRoom);
            SwingUtilities.invokeLater(
                () -> GameContext.getInstance().changeState(GameStateType.PLAYING));
          }
        });
  }

  private void executeAction(CompletableFuture<Room> action) {
    view.setControlsEnabled(false);

    action
        .thenAccept(room -> update(room, true))
        .exceptionally(
            ex -> {
              unlock(false);
              return null;
            });
  }

  private void unlock(boolean isLock) {
    if (!ApplicationSession.getInstance().isOfflineMode() && isLock) {
      return;
    }

    view.setControlsEnabled(true);
  }

  public void onMapLeft() {
    int next = Math.floorMod(currentRoom.getMapIndex() - 1, RoomConstant.MAP_AVATARS.length);
    executeAction(roomService.changeMap(next));
  }

  public void onMapRight() {
    int next = Math.floorMod(currentRoom.getMapIndex() + 1, RoomConstant.MAP_AVATARS.length);
    executeAction(roomService.changeMap(next));
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

    executeAction(roomService.changeCharacter(next));
  }

  public void onSendChat(String content) {
    if (content.isBlank()) return;
    executeAction(roomService.sendChat(content));
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
    executeAction(roomService.startGame());
  }

  public void onToggleReadyClicked() {
    executeAction(roomService.toggleReady());
  }
}
