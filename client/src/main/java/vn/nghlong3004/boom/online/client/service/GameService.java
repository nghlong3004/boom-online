package vn.nghlong3004.boom.online.client.service;

import java.util.function.Consumer;
import vn.nghlong3004.boom.online.client.model.game.GameActionType;
import vn.nghlong3004.boom.online.client.model.response.GameUpdate;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public interface GameService {

  void subscribeToGame(String roomId, Consumer<GameUpdate> onGameUpdate);

  void unsubscribeFromGame();

  void sendMove(float x, float y, String direction);

  void sendPlaceBomb(int tileX, int tileY, int power);

  void sendPlayerHit(String playerId);

  void sendGameEnd(String winnerId, String reason);

  void sendBrickDestroyed(int tileX, int tileY, int tileType);

  void sendItemCollected(String itemId, int tileX, int tileY);

  void sendAction(GameActionType type, Object data);

  boolean isOnlineMode();
}
