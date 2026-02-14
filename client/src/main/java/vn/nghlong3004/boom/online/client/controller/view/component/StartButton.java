package vn.nghlong3004.boom.online.client.controller.view.component;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import javax.swing.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/15/2025
 */
public class StartButton extends JButton {
  public StartButton(String text, boolean primary) {
    super(text);
    setCursor(new Cursor(Cursor.HAND_CURSOR));

    String style = "margin:6,20,6,20; arc:12; font:bold;";
    if (primary) {
      style += "background:$Component.accentColor;";
    }
    putClientProperty(FlatClientProperties.STYLE, style);
  }
}
