package vn.nghlong3004.boom.online.client.model.item;

import java.util.Random;
import vn.nghlong3004.boom.online.client.model.map.TileType;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
public class ItemSpawner {

  private static final float BRICK_SPAWN_CHANCE = 0.4f;
  private static final float GIFT_BOX_SPAWN_CHANCE = 1.0f;

  private final Random random;

  public ItemSpawner() {
    this.random = new Random();
  }

  public Item trySpawnItem(int tileX, int tileY, int tileType) {
    float spawnChance = getSpawnChance(tileType);

    if (random.nextFloat() > spawnChance) {
      return null;
    }

    ItemType type = selectRandomItemType();
    if (type == null) {
      return null;
    }

    return new Item(type, tileX, tileY);
  }

  private float getSpawnChance(int tileType) {
    if (tileType == TileType.GIFT_BOX.id) {
      return GIFT_BOX_SPAWN_CHANCE;
    }
    return BRICK_SPAWN_CHANCE;
  }

  private ItemType selectRandomItemType() {
    float roll = random.nextFloat();
    float cumulativeRate = 0f;

    for (ItemType type : ItemType.values()) {
      cumulativeRate += type.dropRate;
      if (roll < cumulativeRate) {
        return type;
      }
    }

    return ItemType.values()[0];
  }
}
