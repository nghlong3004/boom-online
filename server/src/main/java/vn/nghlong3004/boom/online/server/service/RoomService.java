package vn.nghlong3004.boom.online.server.service;

import vn.nghlong3004.boom.online.server.model.AuthenticatedUser;
import vn.nghlong3004.boom.online.server.model.Room;
import vn.nghlong3004.boom.online.server.model.request.CreateRoomRequest;
import vn.nghlong3004.boom.online.server.model.request.RoomActionRequest;
import vn.nghlong3004.boom.online.server.model.response.RoomPageResponse;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/24/2025
 */
public interface RoomService {
  RoomPageResponse rooms(int pageIndex, int pageSize);

  Room createRoom(CreateRoomRequest request);

  Room joinRoom(String roomId);

  void leaveRoom(String roomId, AuthenticatedUser user);

  Room processAction(String roomId, RoomActionRequest request);

  void handleUserDisconnection(String username);
}
