package vn.nghlong3004.boom.online.client.core;

import java.awt.*;
import vn.nghlong3004.boom.online.client.input.KeyboardAdapter;
import vn.nghlong3004.boom.online.client.input.MouseAdapter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public interface GameLogic extends MouseAdapter, KeyboardAdapter {
  default void update() {}

  void render(Graphics g);
}
