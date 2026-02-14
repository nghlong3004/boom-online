package vn.nghlong3004.boom.online.client.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@Slf4j
public final class ImageUtil {

  private static final Map<String, BufferedImage> CACHE = new ConcurrentHashMap<>();

  public static BufferedImage loadImage(String name) {

    BufferedImage cached = CACHE.get(name);
    if (cached != null) {
      log.debug("Using cached image: {}", name);
      return cached;
    }

    log.info("Loading file name={}", name);
    try (InputStream inputStream = ImageUtil.class.getResourceAsStream(name)) {
      if (inputStream == null) {
        throw new RuntimeException("%s not found!".formatted(name));
      }
      BufferedImage image = ImageIO.read(inputStream);
      CACHE.put(name, image);
      return image;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void clearCache() {
    CACHE.clear();
    log.info("Image cache cleared");
  }

  private ImageUtil() {}
}