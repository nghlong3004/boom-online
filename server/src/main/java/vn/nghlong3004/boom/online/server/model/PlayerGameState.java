package vn.nghlong3004.boom.online.server.model;

import lombok.Data;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Data
public class PlayerGameState {
  private String playerId;
  private String displayName;
  private boolean alive = true;
  private int lives = 1;
  private float x;
  private float y;
  private String direction = "DOWN";

  public PlayerGameState(String playerId, String displayName) {
    this.playerId = playerId;
    this.displayName = displayName;
  }

  public void hit() {
    lives--;
    if (lives <= 0) {
      alive = false;
    }
  }
}
