package vn.nghlong3004.boom.online.server.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Data
public class GameState {
  private String roomId;
  private Map<String, PlayerGameState> players;
  private long startTime;
  private int durationSeconds = 180;
  private boolean running;

  public GameState(String roomId) {
    this.players = new ConcurrentHashMap<>();
    this.roomId = roomId;
    this.startTime = System.currentTimeMillis();
    this.running = true;
  }

  public void addPlayer(String playerId, String displayName) {
    players.put(playerId, new PlayerGameState(playerId, displayName));
  }

  public void markPlayerDead(String playerId) {
    PlayerGameState player = players.get(playerId);
    if (player != null) {
      player.setAlive(false);
    }
  }

  public long getAlivePlayers() {
    return players.values().stream().filter(PlayerGameState::isAlive).count();
  }

  public String getLastAlivePlayerId() {
    return players.values().stream()
        .filter(PlayerGameState::isAlive)
        .map(PlayerGameState::getPlayerId)
        .findFirst()
        .orElse(null);
  }

  public int getRemainingSeconds() {
    long elapsed = (System.currentTimeMillis() - startTime) / 1000;
    return Math.max(0, durationSeconds - (int) elapsed);
  }

  public boolean isTimeExpired() {
    return getRemainingSeconds() <= 0;
  }
}
