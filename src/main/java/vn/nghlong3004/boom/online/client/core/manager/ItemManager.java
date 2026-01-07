package vn.nghlong3004.boom.online.client.core.manager;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.model.bomber.Bomber;
import vn.nghlong3004.boom.online.client.model.item.Item;
import vn.nghlong3004.boom.online.client.model.item.ItemEffect;
import vn.nghlong3004.boom.online.client.model.item.ItemEffectRegistry;
import vn.nghlong3004.boom.online.client.model.item.ItemSpawner;
import vn.nghlong3004.boom.online.client.model.item.ItemType;
import vn.nghlong3004.boom.online.client.renderer.ItemRenderer;
import vn.nghlong3004.boom.online.client.util.TriConsumer;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
@Slf4j
public class ItemManager {

  private final List<Item> items;
  private final ItemRenderer itemRenderer;
  private final ItemSpawner itemSpawner;

  @Setter
  private BiConsumer<Bomber, ItemType> onItemCollected;

  @Setter
  private TriConsumer<String, Integer, Integer> onItemCollectedNetwork;

  public ItemManager() {
    this.items = new ArrayList<>();
    this.itemRenderer = new ItemRenderer();
    this.itemSpawner = new ItemSpawner();
  }

  public void spawnItemAt(int tileX, int tileY) {
    spawnItemAt(tileX, tileY, 2);
  }

  public void spawnItemAt(int tileX, int tileY, int tileType) {
    log.debug("Attempting to spawn item at ({}, {}) with tileType={}", tileX, tileY, tileType);

    if (hasItemAt(tileX, tileY)) {
      log.debug("Item already exists at ({}, {})", tileX, tileY);
      return;
    }

    Item item = itemSpawner.trySpawnItem(tileX, tileY, tileType);
    if (item != null) {
      items.add(item);
      itemRenderer.addItem(item);
      log.info("Item spawned: {} at ({}, {}) id={}", item.getType(), tileX, tileY, item.getId());
    } else {
      log.debug("No item spawned (random chance failed)");
    }
  }

  public void spawnItemFromNetwork(String itemId, int tileX, int tileY, int itemTypeIndex) {
    if (hasItemAt(tileX, tileY)) {
      log.debug("Item already exists at ({}, {})", tileX, tileY);
      return;
    }

    ItemType type = ItemType.fromSpriteIndex(itemTypeIndex);
    if (type == null) {
      log.warn("Unknown item type index: {}", itemTypeIndex);
      return;
    }

    Item item = new Item(itemId, type, tileX, tileY);
    items.add(item);
    itemRenderer.addItem(item);
    log.info("Item spawned from network: {} at ({}, {}) id={}", type, tileX, tileY, itemId);
  }

  public void collectItemFromNetwork(String itemId, int tileX, int tileY) {
    Item item = findItemById(itemId);
    if (item == null) {
      item = findItemAtTile(tileX, tileY);
    }

    if (item != null && item.isActive()) {
      item.collect();
      itemRenderer.removeItem(item);
      log.info("Item collected from network: {} at ({}, {})", itemId, tileX, tileY);
    }
  }

  private Item findItemById(String itemId) {
    return items.stream()
        .filter(item -> item.getId().equals(itemId) && !item.isCollected() && !item.isDestroyed())
        .findFirst()
        .orElse(null);
  }

  private Item findItemAtTile(int tileX, int tileY) {
    return items.stream()
        .filter(item -> item.isAtTile(tileX, tileY) && item.isActive())
        .findFirst()
        .orElse(null);
  }

  public void addItem(Item item) {
    if (item != null && !hasItemAt(item.getTileX(), item.getTileY())) {
      items.add(item);
      itemRenderer.addItem(item);
    }
  }

  public void update() {
    itemRenderer.update();
    updateItems();
  }

  private void updateItems() {
    Iterator<Item> iterator = items.iterator();
    while (iterator.hasNext()) {
      Item item = iterator.next();
      item.update();

      // Chỉ remove item đã collected hoặc destroyed, KHÔNG remove pending
      // vì pending item đang chờ server confirm
      if (item.isCollected() || item.isDestroyed()) {
        itemRenderer.removeItem(item);
        iterator.remove();
      }
    }
  }

  public void checkCollision(Bomber bomber) {
    checkCollision(bomber, false);
  }

  public void checkCollision(Bomber bomber, boolean isOnline) {
    if (bomber == null || !bomber.isAlive()) {
      return;
    }

    int bomberTileX = bomber.getTileX();
    int bomberTileY = bomber.getTileY();

    Iterator<Item> iterator = items.iterator();
    while (iterator.hasNext()) {
      Item item = iterator.next();
      if (item.isActive() && item.isAtTile(bomberTileX, bomberTileY)) {
        log.info("Collision detected: item={} type={} at ({},{}) isOnline={}",
            item.getId(), item.getType(), bomberTileX, bomberTileY, isOnline);
        if (isOnline) {
          item.markPendingCollection();
          log.info("Sending item collected network event for itemId={}", item.getId());
          if (onItemCollectedNetwork != null) {
            onItemCollectedNetwork.accept(item.getId(), item.getTileX(), item.getTileY());
          } else {
            log.warn("onItemCollectedNetwork callback is NULL!");
          }
        } else {
          collectItem(item, bomber);
          itemRenderer.removeItem(item);
          iterator.remove();
        }
        break;
      }
    }
  }

  public void applyItemEffect(String itemId, Bomber bomber) {
    log.info("applyItemEffect: itemId={} bomber={}", itemId, bomber.getDisplayName());
    Item item = findItemById(itemId);
    if (item == null) {
      log.warn("applyItemEffect: Item not found for id={}", itemId);
      return;
    }

    log.info("Applying effect for item type={}", item.getType());
    collectItem(item, bomber);
    itemRenderer.removeItem(item);
  }

  public void checkCollisions(List<Bomber> bombers) {
    if (bombers == null || bombers.isEmpty()) {
      return;
    }

    for (Bomber bomber : bombers) {
      checkCollision(bomber);
    }
  }

  private void collectItem(Item item, Bomber bomber) {
    item.collect();

    ItemEffect effect = ItemEffectRegistry.getEffect(item.getType());
    if (effect != null) {
      effect.apply(bomber);
    }

    if (onItemCollected != null) {
      onItemCollected.accept(bomber, item.getType());
    }
  }

  public void render(Graphics2D g2d) {
    for (Item item : items) {
      if (item.isActive()) {
        itemRenderer.render(g2d, item);
      }
    }
  }

  public void destroyItemsAt(int tileX, int tileY) {
    for (Item item : items) {
      if (item.isActive() && item.isAtTile(tileX, tileY)) {
        item.destroy();
      }
    }
  }

  public boolean hasItemAt(int tileX, int tileY) {
    return items.stream().anyMatch(item -> item.isActive() && item.isAtTile(tileX, tileY));
  }

  public void clear() {
    items.clear();
    itemRenderer.clear();
  }

  public int getItemCount() {
    return items.size();
  }
}
