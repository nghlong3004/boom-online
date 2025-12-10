package com.vn.nghlong3004.client.view.component;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import javax.swing.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/7/2025
 */
public class ButtonLink extends JButton {
  public ButtonLink(String text) {
    super(text);
    setFocusPainted(false);
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    putClientProperty(
        FlatClientProperties.STYLE,
        "arc:15;"
            + "margin:1,5,1,5;"
            + "borderWidth:0;"
            + "focusWidth:0;"
            + "innerFocusWidth:0;"
            + "foreground:$Component.accentColor;"
            + "background:null;");
  }

  @Override
  public boolean isDefaultButton() {
    return true;
  }
}
