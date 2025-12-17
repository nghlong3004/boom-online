package vn.nghlong3004.boom.online.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public interface KeyboardAdapter extends KeyListener {
  default void keyTyped(KeyEvent e) {}

  default void keyPressed(KeyEvent e) {}

  default void keyReleased(KeyEvent e) {}
}
