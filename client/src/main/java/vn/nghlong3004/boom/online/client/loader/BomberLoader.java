package vn.nghlong3004.boom.online.client.loader;

import static vn.nghlong3004.boom.online.client.constant.BomberConstant.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.model.bomber.BomberType;
import vn.nghlong3004.boom.online.client.util.ImageUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Slf4j
public class BomberLoader {

  public static List<BufferedImage[][]> loadBomberAssets() {
    var images = new ArrayList<BufferedImage[][]>();
    for (var type : BomberType.values()) {
      String name = BOMBER_SKIN_TEMPLATE.formatted(type.getAssetKey());
      var image = loadBomberAsset(name);
      images.add(image);
    }
    return images;
  }

  public static BufferedImage[] loadBomberDeathAssets() {
    log.info("Loading bomber death animation sprites");
    BufferedImage deathSheet = ImageUtil.loadImage(BOMBER_DEAD);
    BufferedImage[] frames = new BufferedImage[BOMBER_DEAD_FRAMES];

    for (int i = 0; i < BOMBER_DEAD_FRAMES; i++) {
      int col = i % BOMBER_DEAD_COLS;
      frames[i] =
          deathSheet.getSubimage(
              col * BOMBER_DEAD_SPRITE_SIZE, 0, BOMBER_DEAD_SPRITE_SIZE, BOMBER_DEAD_SPRITE_SIZE);
    }

    log.info("Loaded {} bomber death animation frames", BOMBER_DEAD_FRAMES);
    return frames;
  }

  private static BufferedImage[][] loadBomberAsset(String name) {
    BufferedImage[][] animations = new BufferedImage[4][5];
    BufferedImage image = ImageUtil.loadImage(name);
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 5; ++j) {
        animations[i][j] =
            image.getSubimage(
                j * BOMBER_WIDTH_IMAGE,
                i * BOMBER_HEIGHT_IMAGE,
                BOMBER_WIDTH_IMAGE,
                BOMBER_HEIGHT_IMAGE);
      }
    }
    return animations;
  }
}
