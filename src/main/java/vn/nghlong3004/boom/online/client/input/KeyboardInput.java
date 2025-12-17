package vn.nghlong3004.boom.online.client.input;

import java.awt.event.KeyEvent;
import lombok.RequiredArgsConstructor;
import vn.nghlong3004.boom.online.client.core.GamePanel;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@RequiredArgsConstructor
public class KeyboardInput implements KeyboardAdapter {

  private final GamePanel gamePanel;

  @Override
  public void keyPressed(KeyEvent e) {
    gamePanel.getGameContext().keyPressed(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    gamePanel.getGameContext().keyReleased(e);
  }
}
