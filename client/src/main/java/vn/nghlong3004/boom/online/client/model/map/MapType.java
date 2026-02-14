package vn.nghlong3004.boom.online.client.model.map;

import lombok.AllArgsConstructor;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@AllArgsConstructor
public enum MapType {
  DESERT_MODE(0),
  LAND_MODE(1),
  TOWN_MODE(2),
  UNDERWATER_MODE(3),
  XMAS_MODE(4);
  public final int id;

  public String getAssetKey() {
    return this.name().toLowerCase();
  }

  public String getName() {
    return this.name().substring(0, this.name().indexOf("_")).toUpperCase();
  }
}
