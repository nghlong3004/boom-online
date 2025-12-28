package vn.nghlong3004.boom.online.client.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
public final class DebouncerUtil {
  private static final Map<String, Timer> TIMERS = new ConcurrentHashMap<>();

  public static void debounce(String key, int delay, Runnable action) {
    if (TIMERS.containsKey(key)) {
      TIMERS.get(key).stop();
    }

    Timer timer =
        new Timer(
            delay,
            e -> {
              action.run();
              TIMERS.remove(key);
            });
    timer.setRepeats(false);
    timer.start();
    TIMERS.put(key, timer);
  }
}
