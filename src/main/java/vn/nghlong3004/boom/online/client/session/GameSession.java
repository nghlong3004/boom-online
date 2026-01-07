package vn.nghlong3004.boom.online.client.session;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.model.response.GameUpdate;
import vn.nghlong3004.boom.online.client.service.GameService;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Slf4j
@Getter
public class GameSession {

  private static final int DEFAULT_GAME_DURATION_SECONDS = 180;

  @Setter
  private GameService gameService;

  @Setter
  private String roomId;

  @Setter
  private Consumer<GameUpdate> gameUpdateHandler;

  private boolean online;
  private boolean gameRunning;
  private long startTimeMillis;
  private int gameDurationSeconds;
  @Setter
  private Runnable onTimeExpired;
  private boolean timeExpiredFired;

  private GameSession() {
    this.gameDurationSeconds = DEFAULT_GAME_DURATION_SECONDS;
  }

  public static GameSession getInstance() {
    return Holder.INSTANCE;
  }

  public void startOnlineGame(String roomId, Consumer<GameUpdate> handler) {
    this.roomId = roomId;
    this.gameUpdateHandler = handler;
    this.online = true;
    startTimer();

    if (gameService != null) {
      gameService.subscribeToGame(roomId, this::handleGameUpdate);
    }

    log.info("Started online game for room: {} with {} seconds", roomId, gameDurationSeconds);
  }

  public void startOfflineGame() {
    this.online = false;
    this.roomId = null;
    this.gameUpdateHandler = null;
    startTimer();
    log.info("Started offline game with {} seconds", gameDurationSeconds);
  }

  private void startTimer() {
    this.startTimeMillis = System.currentTimeMillis();
    this.gameRunning = true;
    this.timeExpiredFired = false;
  }

  public void endGame() {
    if (gameService != null && online) {
      gameService.unsubscribeFromGame();
    }
    this.online = false;
    this.roomId = null;
    this.gameUpdateHandler = null;
    this.gameRunning = false;
    this.onTimeExpired = null;
    this.timeExpiredFired = false;
    log.info("Game ended");
  }

  public void updateTimer() {
    if (!gameRunning || timeExpiredFired) {
      return;
    }

    if (getRemainingSeconds() <= 0) {
      timeExpiredFired = true;
      gameRunning = false;
      log.info("Game time expired!");
      if (onTimeExpired != null) {
        onTimeExpired.run();
      }
    }
  }

  public int getRemainingSeconds() {
    if (!gameRunning && startTimeMillis == 0) {
      return gameDurationSeconds;
    }
    long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
    int elapsedSeconds = (int) (elapsedMillis / 1000);
    return Math.max(0, gameDurationSeconds - elapsedSeconds);
  }

  public String getFormattedTime() {
    int seconds = getRemainingSeconds();
    int minutes = seconds / 60;
    int secs = seconds % 60;
    return String.format("%d:%02d", minutes, secs);
  }

  public void sendMove(float x, float y, String direction) {
    if (online && gameService != null) {
      gameService.sendMove(x, y, direction);
    }
  }

  public void sendPlaceBomb(int tileX, int tileY, int power) {
    if (online && gameService != null) {
      gameService.sendPlaceBomb(tileX, tileY, power);
    }
  }

  public void sendPlayerHit(String playerId) {
    if (online && gameService != null) {
      log.info("Send playerHit by playerId:{}", playerId);
      gameService.sendPlayerHit(playerId);
    }
  }

  public void sendBrickDestroyed(int tileX, int tileY, int tileType) {
    if (online && gameService != null) {
      gameService.sendBrickDestroyed(tileX, tileY, tileType);
    }
  }

  public void sendItemCollected(String itemId, int tileX, int tileY) {
    if (online && gameService != null) {
      gameService.sendItemCollected(itemId, tileX, tileY);
    }
  }

  private void handleGameUpdate(GameUpdate update) {
    if (gameUpdateHandler != null) {
      gameUpdateHandler.accept(update);
    }
  }

  public boolean isOnline() {
    return online && gameService != null && gameService.isOnlineMode();
  }

  private static class Holder {
    private static final GameSession INSTANCE = new GameSession();
  }
}
