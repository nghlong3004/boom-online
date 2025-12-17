package vn.nghlong3004.boom.online.client.input;

import java.awt.event.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public interface MouseAdapter extends MouseListener, MouseWheelListener, MouseMotionListener {
  default void mouseClicked(MouseEvent e) {}

  default void mousePressed(MouseEvent e) {}

  default void mouseReleased(MouseEvent e) {}

  default void mouseEntered(MouseEvent e) {}

  default void mouseExited(MouseEvent e) {}

  default void mouseDragged(MouseEvent e) {}

  default void mouseMoved(MouseEvent e) {}

  default void mouseWheelMoved(MouseWheelEvent e) {}
}
