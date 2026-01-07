package vn.nghlong3004.boom.online.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
@Getter
@AllArgsConstructor
public enum TileType {
  STONE(0),
  FLOOR(1),
  BRICK(2),
  GIFT_BOX(3);
  private final int id;
}
