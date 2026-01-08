package vn.nghlong3004.boom.online.client.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
@Slf4j
public final class BrowserUtil {

  private BrowserUtil() {}

  public static boolean openUrl(String url) {
    if (url == null || url.isBlank()) {
      return false;
    }
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(new URI(url));
        return true;
      }
      return openWithCommand(url);
    } catch (Exception e) {
      log.error("Failed to open browser", e);
      return false;
    }
  }

  private static boolean openWithCommand(String url) throws IOException {
    String operationSystem = System.getProperty("os.name").toLowerCase();
    Runtime runtime = Runtime.getRuntime();

    if (operationSystem.contains("win")) {
      runtime.exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", url});
    } else if (operationSystem.contains("mac")) {
      runtime.exec(new String[] {"open", url});
    } else {
      runtime.exec(new String[] {"xdg-open", url});
    }
    return true;
  }
}
