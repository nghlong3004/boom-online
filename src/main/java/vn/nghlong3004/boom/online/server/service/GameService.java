package vn.nghlong3004.boom.online.server.service;

import vn.nghlong3004.boom.online.server.model.Room;
import vn.nghlong3004.boom.online.server.model.request.GameActionRequest;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public interface GameService {

  void startGame(Room room);

  void handleAction(String roomId, GameActionRequest request);

  void handleDisconnect(String roomId, String playerId);

  void endGame(String roomId, String winnerId, String reason);
}
