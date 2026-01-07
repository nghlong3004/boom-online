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
public enum ItemType {
  BOMB_UP(0, 0.4f),
  FIRE_UP(1, 0.35f),
  SPEED_UP(2, 0.25f);

  private final int spriteIndex;
  private final float dropRate;
}
