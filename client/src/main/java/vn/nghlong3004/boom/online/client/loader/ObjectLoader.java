package vn.nghlong3004.boom.online.client.loader;

import static vn.nghlong3004.boom.online.client.constant.ObjectConstant.*;
import static vn.nghlong3004.boom.online.client.util.ImageUtil.loadImage;

import java.awt.image.BufferedImage;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Slf4j
public class ObjectLoader {

  public static BufferedImage[] loadBombAssets() {
    log.info("Loading custom bomb sprites");
    BufferedImage bombSheet = loadImage(CUSTOM_BOMB);
    BufferedImage[] bombSprites = new BufferedImage[CUSTOM_BOMB_FRAMES];

    if (bombSheet == null) {
      log.error("Failed to load custom bomb sprite sheet");
      return bombSprites;
    }

    for (int i = 0; i < CUSTOM_BOMB_FRAMES; i++) {
      bombSprites[i] =
          bombSheet.getSubimage(
              i * CUSTOM_BOMB_SPRITE_SIZE, 0, CUSTOM_BOMB_SPRITE_SIZE, CUSTOM_BOMB_SPRITE_SIZE);
    }

    log.info("Loaded {} custom bomb frames", CUSTOM_BOMB_FRAMES);
    return bombSprites;
  }

  public static BufferedImage[] loadExplosionAnimationFrameAssets() {
    log.info("Loading explosion animation frames");
    BufferedImage explosionSheet = loadImage(EXPLOSION_ANIMATION);
    BufferedImage[] frames = new BufferedImage[EXPLOSION_FRAME_COUNT];

    for (int i = 0; i < EXPLOSION_FRAME_COUNT; i++) {
      frames[i] =
          explosionSheet.getSubimage(
              i * EXPLOSION_FRAME_WIDTH, 0, EXPLOSION_FRAME_WIDTH, EXPLOSION_FRAME_HEIGHT);
    }

    return frames;
  }

  public static BufferedImage[][] loadItemAssets() {
    log.info("Loading item images");
    BufferedImage[][] items = new BufferedImage[ITEMS][ITEM_FRAMES];
    items[0] = loadItem(ITEM_BOMB);
    items[1] = loadItem(ITEM_BOMB_SIZE);
    items[2] = loadItem(ITEM_SHOE);
    log.info("Loaded {} item images", items.length);
    return items;
  }

  private static BufferedImage[] loadItem(String item) {
    log.info("Loading item animation frames");
    BufferedImage explosionSheet = loadImage(item);
    BufferedImage[] frames = new BufferedImage[ITEM_FRAMES];

    for (int i = 0; i < ITEM_FRAMES; i++) {
      frames[i] = explosionSheet.getSubimage(i * ITEM_WIDTH, 0, ITEM_WIDTH, ITEM_HEIGHT);
    }

    return frames;
  }

  public static BufferedImage[] loadItemEffectFramesAssets() {
    log.info("Loading item effect frames");
    BufferedImage[] effects = new BufferedImage[ITEM_EFFECT_FRAMES];
    for (int i = 0; i < ITEM_EFFECT_FRAMES; i++) {
      effects[i] = loadImage(ITEM_EFFECT_TEMPLATE.formatted(i + 1));
    }
    log.info("Loaded {} item effect frames", ITEM_EFFECT_FRAMES);
    return effects;
  }

  public static BufferedImage[] loadMoveEffectAssets() {
    log.info("Loading move effect frames");
    BufferedImage moveSheet = loadImage(MOVE_EFFECT);
    BufferedImage[] frames = new BufferedImage[MOVE_EFFECT_FRAMES];

    if (moveSheet == null) {
      log.error("Failed to load move effect sprite sheet");
      return frames;
    }

    for (int i = 0; i < MOVE_EFFECT_FRAMES; i++) {
      frames[i] =
          moveSheet.getSubimage(
              i * MOVE_EFFECT_FRAME_WIDTH, 0, MOVE_EFFECT_FRAME_WIDTH, MOVE_EFFECT_FRAME_HEIGHT);
    }

    log.info("Loaded {} move effect frames", MOVE_EFFECT_FRAMES);
    return frames;
  }

  private ObjectLoader() {}
}
