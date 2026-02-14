package vn.nghlong3004.boom.online.client.core;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.*;
import javax.swing.*;
import vn.nghlong3004.boom.online.client.util.I18NUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public class GameLaunch {
  public static void run() {
    FlatRobotoFont.install();
    FlatLaf.registerCustomDefaultsSource("themes");
    FlatMacLightLaf.setup();
    UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
    I18NUtil.registerDefaultLanguage();
    GameCanvas gameCanvas = GameFactory.createGameCanvas();
    EventQueue.invokeLater(gameCanvas::start);
    gameCanvas.registerShutdownHook();
  }
}
