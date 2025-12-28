package vn.nghlong3004.boom.online.client.service.impl;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import vn.nghlong3004.boom.online.client.model.RoomActionType;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.request.CreateRoomRequest;
import vn.nghlong3004.boom.online.client.model.request.RoomActionRequest;
import vn.nghlong3004.boom.online.client.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.client.model.room.*;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

  private final HttpService httpService;
  private final WebSocketService webSocketService;
  @Getter @Setter private Room currentRoom;

  @Override
  public CompletableFuture<RoomPageResponse> rooms(int pageIndex, int pageSize) {
    String token = UserSession.getInstance().getAccessToken();
    return httpService.getRooms(pageIndex, pageSize, token);
  }

  @Override
  public CompletableFuture<Room> joinRoom(String roomId, User user) {
    String token = UserSession.getInstance().getAccessToken();
    return httpService.joinRoom(roomId, token);
  }

  @Override
  public CompletableFuture<Room> createRoom(User owner, String roomName) {
    return httpService.createRoom(
        new CreateRoomRequest(roomName, 0), UserSession.getInstance().getAccessToken());
  }

  @Override
  public CompletableFuture<Room> changeMap(int mapIndex) {
    send(RoomActionType.CHANGE_MAP, mapIndex);
    return CompletableFuture.completedFuture(currentRoom);
  }

  @Override
  public CompletableFuture<Room> changeCharacter(int characterIndex) {
    send(RoomActionType.CHANGE_CHARACTER, characterIndex);
    return CompletableFuture.completedFuture(currentRoom);
  }

  @Override
  public CompletableFuture<Room> sendChat(String content) {
    send(RoomActionType.CHAT, content);
    return CompletableFuture.completedFuture(currentRoom);
  }

  @Override
  public CompletableFuture<Room> toggleReady() {
    send(RoomActionType.READY, null);
    return CompletableFuture.completedFuture(currentRoom);
  }

  @Override
  public CompletableFuture<Room> startGame() {
    send(RoomActionType.START, null);
    return CompletableFuture.completedFuture(currentRoom);
  }

  private void send(RoomActionType type, Object data) {
    String destination = "/app/room/" + currentRoom.getId() + "/action";
    String email = UserSession.getInstance().getCurrentUser().getEmail();
    webSocketService.send(destination, new RoomActionRequest(type, data, email));
  }
}
