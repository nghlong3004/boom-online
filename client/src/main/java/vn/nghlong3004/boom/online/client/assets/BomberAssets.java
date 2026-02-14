package vn.nghlong3004.boom.online.client.assets;

import java.awt.image.BufferedImage;
import java.util.List;
import lombok.Getter;
import vn.nghlong3004.boom.online.client.loader.BomberLoader;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Getter
public class BomberAssets {
  private final List<BufferedImage[][]> bomberAssets;
  private final BufferedImage[] bomberDeathAssets;

  public static BomberAssets getInstance() {
    return BomberAssets.Holder.INSTANCE;
  }

  private BomberAssets() {
    bomberAssets = BomberLoader.loadBomberAssets();
    bomberDeathAssets = BomberLoader.loadBomberDeathAssets();
  }

  private static class Holder {
    private static final BomberAssets INSTANCE = new BomberAssets();
  }
}
