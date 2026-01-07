package vn.nghlong3004.boom.online.client.assets;

import java.awt.image.BufferedImage;
import lombok.Getter;
import vn.nghlong3004.boom.online.client.loader.ObjectLoader;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Getter
public class ObjectAssets {

  private final BufferedImage[] bombAssets;

  private final BufferedImage[] explosionAnimationFrameAssets;

  private final BufferedImage[][] itemAssets;
  private final BufferedImage[] moveEffectAssets;

  public static ObjectAssets getInstance() {
    return ObjectAssets.Holder.INSTANCE;
  }

  private ObjectAssets() {
    bombAssets = ObjectLoader.loadBombAssets();
    explosionAnimationFrameAssets = ObjectLoader.loadExplosionAnimationFrameAssets();
    itemAssets = ObjectLoader.loadItemAssets();
    moveEffectAssets = ObjectLoader.loadMoveEffectAssets();
  }

  private static class Holder {
    private static final ObjectAssets INSTANCE = new ObjectAssets();
  }
}
