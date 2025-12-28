package vn.nghlong3004.boom.online.client.service;

import java.util.function.Consumer;
import vn.nghlong3004.boom.online.client.model.room.Room;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
public interface WebSocketService {

  void connectAndSubscribe(String token, String roomId, Consumer<Room> onRoomUpdate);

  void send(String destination, Object payload);

  void disconnect();
}
