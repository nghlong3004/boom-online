package vn.nghlong3004.boom.online.client.input;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import vn.nghlong3004.boom.online.client.model.bomber.Direction;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class InputHandler {

  private final Set<Integer> pressedKeys;
  @Getter private Direction currentDirection;

  public InputHandler() {
    this.pressedKeys = new HashSet<>();
    this.currentDirection = null;
  }

  public void keyPressed(int keyCode) {
    pressedKeys.add(keyCode);
    updateDirection();
  }

  public void keyReleased(int keyCode) {
    pressedKeys.remove(keyCode);
    updateDirection();
  }

  public boolean isMoving() {
    return currentDirection != null;
  }

  public void reset() {
    pressedKeys.clear();
    currentDirection = null;
  }

  private void updateDirection() {
    currentDirection = null;
    if (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_W)) {
      currentDirection = Direction.UP;
    } else if (pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S)) {
      currentDirection = Direction.DOWN;
    } else if (pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A)) {
      currentDirection = Direction.LEFT;
    } else if (pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D)) {
      currentDirection = Direction.RIGHT;
    }
  }
}
