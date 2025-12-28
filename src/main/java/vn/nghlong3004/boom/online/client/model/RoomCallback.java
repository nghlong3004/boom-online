package vn.nghlong3004.boom.online.client.model;

import vn.nghlong3004.boom.online.client.model.room.Room;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@FunctionalInterface
public interface RoomCallback {
  void execute(Room room);
}
