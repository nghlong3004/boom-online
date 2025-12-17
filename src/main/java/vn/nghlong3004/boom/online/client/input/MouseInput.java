package vn.nghlong3004.boom.online.client.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import lombok.RequiredArgsConstructor;
import vn.nghlong3004.boom.online.client.core.GamePanel;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@RequiredArgsConstructor
public class MouseInput implements MouseAdapter {

  private final GamePanel gamePanel;

  @Override
  public void mouseClicked(MouseEvent e) {
    gamePanel.getGameContext().mouseClicked(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    gamePanel.getGameContext().mousePressed(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    gamePanel.getGameContext().mouseReleased(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    gamePanel.getGameContext().mouseDragged(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    gamePanel.getGameContext().mouseMoved(e);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    gamePanel.getGameContext().mouseWheelMoved(e);
  }
}
