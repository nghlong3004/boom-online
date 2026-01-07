package vn.nghlong3004.boom.online.server.util;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.nghlong3004.boom.online.server.model.ItemSpawnedData;
import vn.nghlong3004.boom.online.server.model.ItemType;
import vn.nghlong3004.boom.online.server.model.TileType;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
@Component
@RequiredArgsConstructor
public class ItemSpawner {

  private static final float BRICK_SPAWN_CHANCE = 0.4f;
  private static final float GIFT_BOX_SPAWN_CHANCE = 1.0f;

  private final GameRandomUtils gameRandomUtils;

  public ItemSpawnedData trySpawnItem(int tileX, int tileY, int tileType) {
    float spawnChance = getSpawnChance(tileType);

    if (gameRandomUtils.nextDouble() > spawnChance) {
      return null;
    }

    ItemType type = selectRandomItemType();
    if (type == null) {
      return null;
    }

    String itemId = UUID.randomUUID().toString();

    return new ItemSpawnedData(itemId, tileX, tileY, type.getSpriteIndex());
  }

  private float getSpawnChance(int tileType) {
    if (tileType == TileType.GIFT_BOX.getId()) {
      return GIFT_BOX_SPAWN_CHANCE;
    }
    return BRICK_SPAWN_CHANCE;
  }

  private ItemType selectRandomItemType() {
    double roll = gameRandomUtils.nextDouble();
    double cumulativeRate = 0f;

    for (ItemType type : ItemType.values()) {
      cumulativeRate += type.getDropRate();
      if (roll < cumulativeRate) {
        return type;
      }
    }

    return ItemType.values()[0];
  }
}
