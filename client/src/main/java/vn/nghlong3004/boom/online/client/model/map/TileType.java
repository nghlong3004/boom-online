package vn.nghlong3004.boom.online.client.model.map;

import lombok.AllArgsConstructor;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@AllArgsConstructor
public enum TileType {
  STONE(0),
  FLOOR(1),
  BRICK(2),
  GIFT_BOX(3);
  public final int id;

  public String getAssetKey() {
    return this.name().toLowerCase();
  }
}
