package vn.nghlong3004.boom.online.client.service.impl;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.model.game.GameActionType;
import vn.nghlong3004.boom.online.client.model.game.GameEndData;
import vn.nghlong3004.boom.online.client.model.game.MoveData;
import vn.nghlong3004.boom.online.client.model.game.PlaceBombData;
import vn.nghlong3004.boom.online.client.model.game.PlayerHitData;
import vn.nghlong3004.boom.online.client.model.request.GameActionRequest;
import vn.nghlong3004.boom.online.client.model.response.GameUpdate;
import vn.nghlong3004.boom.online.client.service.GameService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

  private final WebSocketService webSocketService;

  @Setter
  private String currentRoomId;

  @Override
  public void subscribeToGame(String roomId, Consumer<GameUpdate> onGameUpdate) {
    this.currentRoomId = roomId;
    webSocketService.subscribeToGame(roomId, onGameUpdate);
    log.info("Subscribed to game: {}", roomId);
  }

  @Override
  public void unsubscribeFromGame() {
    webSocketService.unsubscribeFromGame();
    this.currentRoomId = null;
    log.info("Unsubscribed from game");
  }

  @Override
  public void sendMove(float x, float y, String direction) {
    MoveData data = new MoveData(x, y, direction);
    sendAction(GameActionType.MOVE, data);
  }

  @Override
  public void sendPlaceBomb(int tileX, int tileY, int power) {
    PlaceBombData data = new PlaceBombData(tileX, tileY, power);
    sendAction(GameActionType.PLACE_BOMB, data);
  }

  @Override
  public void sendPlayerHit(String playerId) {
    PlayerHitData data = new PlayerHitData(playerId);
    sendAction(GameActionType.PLAYER_HIT, data);
  }

  @Override
  public void sendGameEnd(String winnerId, String reason) {
    GameEndData data = new GameEndData(winnerId, reason);
    sendAction(GameActionType.GAME_END, data);
    log.info("Sent game end - winnerId: {}, reason: {}", winnerId, reason);
  }

  @Override
  public void sendAction(GameActionType type, Object data) {
    if (!isOnlineMode()) {
      return;
    }

    String playerId = getPlayerId();
    String destination = "/app/game/" + currentRoomId + "/action";
    GameActionRequest request = new GameActionRequest(type, data, playerId);

    webSocketService.send(destination, request);
    log.debug("Sent game action: {} to {}", type, destination);
  }

  @Override
  public boolean isOnlineMode() {
    return webSocketService.isConnected() && currentRoomId != null;
  }

  private String getPlayerId() {
    if (UserSession.getInstance().getCurrentUser() != null) {
      return String.valueOf(UserSession.getInstance().getCurrentUser().getId());
    }
    return "offline-player";
  }
}
