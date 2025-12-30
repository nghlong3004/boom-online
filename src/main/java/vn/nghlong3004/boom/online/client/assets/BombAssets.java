package vn.nghlong3004.boom.online.client.assets;

import java.awt.image.BufferedImage;
import lombok.Getter;
import vn.nghlong3004.boom.online.client.loader.ObjectLoader;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Getter
public class BombAssets {

  private final BufferedImage[] bombFrames;
  private final BufferedImage[] explosionFrames;

  private BombAssets() {
    this.bombFrames = ObjectLoader.loadBombAssets();
    this.explosionFrames = ObjectLoader.loadExplosionAnimationFrameAssets();
  }

  public static BombAssets getInstance() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    private static final BombAssets INSTANCE = new BombAssets();
  }
}
