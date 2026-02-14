package vn.nghlong3004.boom.online.client.controller.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import lombok.RequiredArgsConstructor;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@RequiredArgsConstructor
public abstract class FormPanel extends JPanel {

  protected void installRevealButton(JPasswordField txt) {
    FlatSVGIcon iconEye = new FlatSVGIcon("images/eye.svg", 0.3f);
    FlatSVGIcon iconHide = new FlatSVGIcon("images/hide.svg", 0.3f);

    JToolBar toolBar = new JToolBar();
    toolBar.putClientProperty(FlatClientProperties.STYLE, "margin:0,0,0,5;");
    JButton button = new JButton(iconEye);

    button.addActionListener(
        new ActionListener() {

          private final char defaultEchoChart = txt.getEchoChar();
          private boolean show;

          @Override
          public void actionPerformed(ActionEvent actionEvent) {
            show = !show;
            if (show) {
              button.setIcon(iconHide);
              txt.setEchoChar((char) 0);
            } else {
              button.setIcon(iconEye);
              txt.setEchoChar(defaultEchoChart);
            }
          }
        });
    toolBar.add(button);
    txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
  }
}
