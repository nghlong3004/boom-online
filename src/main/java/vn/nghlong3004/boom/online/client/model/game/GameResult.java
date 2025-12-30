package vn.nghlong3004.boom.online.client.model.game;

import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Getter
public enum GameResult {
  WIN("VICTORY!", "You are the last one standing!"),
  LOSE("DEFEATED!", "Better luck next time!"),
  DRAW("DRAW!", "Time's up! No winner this round.");

  private final String title;
  private final String message;

  GameResult(String title, String message) {
    this.title = title;
    this.message = message;
  }
}
