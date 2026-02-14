package vn.nghlong3004.boom.online.client.assets;

import java.awt.image.BufferedImage;
import lombok.Getter;
import vn.nghlong3004.boom.online.client.loader.MapLoader;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
public class MapAssets {
  @Getter private final BufferedImage[][] mapAssets;

  public static MapAssets getInstance() {
    return Holder.INSTANCE;
  }

  private MapAssets() {
    mapAssets = MapLoader.loadMapAssets();
  }

  private static class Holder {
    private static final MapAssets INSTANCE = new MapAssets();
  }
}
