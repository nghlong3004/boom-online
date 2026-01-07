package vn.nghlong3004.boom.online.client.model.item;

import lombok.AllArgsConstructor;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
@AllArgsConstructor
public enum ItemType {
  BOMB_UP(0, 0.4f),
  FIRE_UP(1, 0.35f),
  SPEED_UP(2, 0.25f);

  public final int spriteIndex;
  public final float dropRate;

  public static ItemType fromSpriteIndex(int index) {
    for (ItemType type : values()) {
      if (type.spriteIndex == index) {
        return type;
      }
    }
    return null;
  }
}
