package vn.nghlong3004.boom.online.client.model.bomber;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Getter
@AllArgsConstructor
public enum Direction {
  DOWN(0, 0, 1),
  LEFT(1, -1, 0),
  RIGHT(2, 1, 0),
  UP(3, 0, -1);

  private final int animationRow;
  private final int deltaX;
  private final int deltaY;

  public static Direction fromKeyCode(int keyCode) {
    return switch (keyCode) {
      case java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_W -> UP;
      case java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.VK_S -> DOWN;
      case java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.VK_A -> LEFT;
      case java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.KeyEvent.VK_D -> RIGHT;
      default -> null;
    };
  }
}
