package vn.nghlong3004.boom.online.server.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
@Component
public class GameRandomUtils {

  public <T> T pick(List<T> items) {
    return items.get(ThreadLocalRandom.current().nextInt(items.size()));
  }

  public <T> T pick(T[] items) {
    return items[ThreadLocalRandom.current().nextInt(items.length)];
  }

  public int nextInt(int min, int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public double nextDouble() {
    return ThreadLocalRandom.current().nextDouble();
  }
}
