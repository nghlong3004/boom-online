package vn.nghlong3004.boom.online.client.controller.view.component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public class LabelAdapter extends JLabel {

  public LabelAdapter(String text) {
    super("<html><a href=\"#\">" + text + "</a></html>");
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    setFocusable(true);
  }

  public LabelAdapter() {
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    setFocusable(true);
  }

  public void addOnClick(Consumer<Void> event) {
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
              requestFocus();
              event.accept(null);
            }
          }
        });
  }
}
