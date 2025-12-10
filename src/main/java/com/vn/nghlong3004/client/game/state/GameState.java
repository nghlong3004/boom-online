package com.vn.nghlong3004.client.game.state;

import com.vn.nghlong3004.client.context.GameContext;
import com.vn.nghlong3004.client.game.GameAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public interface GameState extends GameAdapter {
  void previous(GameContext gameContext);

  void next(GameContext gameContext);

  void print();

  default void mouseClicked(MouseEvent e) {}

  default void mousePressed(MouseEvent e) {}

  default void mouseReleased(MouseEvent e) {}

  default void mouseDragged(MouseEvent e) {}

  default void mouseMoved(MouseEvent e) {}

  default void keyPressed(KeyEvent e) {}

  default void keyReleased(KeyEvent e) {}

  default void update() {}
}
