package vn.nghlong3004.boom.online.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import vn.nghlong3004.boom.online.client.assets.MapAssets;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.model.map.GameMap;
import vn.nghlong3004.boom.online.client.model.map.TileType;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class MapRenderer {

  private final BufferedImage[][] tileImages;

  public MapRenderer() {
    this.tileImages = MapAssets.getInstance().getMapAssets();
  }

  public void render(Graphics g, GameMap gameMap) {
    if (gameMap == null) {
      return;
    }

    int mapTypeId = gameMap.getMapType().id;

    for (int row = 0; row < gameMap.getRows(); row++) {
      for (int col = 0; col < gameMap.getCols(); col++) {
        int tileValue = gameMap.getTile(row, col);
        int tileTypeId = mapTileValueToTileType(tileValue);

        BufferedImage tileImage = tileImages[mapTypeId][tileTypeId];

        int x = col * GameConstant.TILE_SIZE;
        int y = row * GameConstant.TILE_SIZE;

        if (tileImage != null) {
          g.drawImage(tileImage, x, y, GameConstant.TILE_SIZE, GameConstant.TILE_SIZE, null);
        }
      }
    }
  }

  private int mapTileValueToTileType(int tileValue) {
    return switch (tileValue) {
      case 0 -> TileType.STONE.id;
      case 2 -> TileType.BRICK.id;
      case 3 -> TileType.GIFT_BOX.id;
      default -> TileType.FLOOR.id;
    };
  }
}
