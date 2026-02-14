package vn.nghlong3004.boom.online.client.service;

import java.util.concurrent.CompletableFuture;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.client.model.room.Room;

/**
 * Project: boom-online-client
 *
 * <p>Offline: InMemoryRoomServiceImpl Online: RoomServiceImpl
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
public interface RoomService {
  CompletableFuture<Room> createRoom(User owner, String roomName);

  CompletableFuture<RoomPageResponse> rooms(int pageIndex, int pageSize);

  CompletableFuture<Room> joinRoom(String roomId, User user);

  CompletableFuture<Room> changeMap(int mapIndex);

  CompletableFuture<Room> changeCharacter(int characterIndex);

  CompletableFuture<Room> sendChat(String content);

  CompletableFuture<Room> toggleReady();

  CompletableFuture<Room> startGame();

  default void setCurrentRoom(Room room) {}
}
