package vn.nghlong3004.boom.online.client.model.item;

import java.util.UUID;
import lombok.Getter;
import vn.nghlong3004.boom.online.client.constant.GameConstant;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
@Getter
public class Item {

    private static final int SPAWN_PROTECTION_TICKS = 60;

    private final String id;
    private final ItemType type;
    private final int tileX;
    private final int tileY;

    private boolean collected;
    private boolean destroyed;
    private boolean pendingCollection;
    private int spawnProtectionTimer;

    public Item(ItemType type, int tileX, int tileY) {
        this(UUID.randomUUID().toString(), type, tileX, tileY);
    }

    public Item(String id, ItemType type, int tileX, int tileY) {
        this.id = id;
        this.type = type;
        this.tileX = tileX;
        this.tileY = tileY;
        this.collected = false;
        this.destroyed = false;
        this.pendingCollection = false;
        this.spawnProtectionTimer = SPAWN_PROTECTION_TICKS;
    }

    public void update() {
        if (spawnProtectionTimer > 0) {
            spawnProtectionTimer--;
        }
    }

    public void collect() {
        this.collected = true;
    }

    public void markPendingCollection() {
        this.pendingCollection = true;
    }

    public boolean isPendingCollection() {
        return pendingCollection;
    }

    public void destroy() {
        if (spawnProtectionTimer <= 0) {
            this.destroyed = true;
        }
    }

    public boolean isActive() {
        return !collected && !destroyed && !pendingCollection;
    }

    public boolean isProtected() {
        return spawnProtectionTimer > 0;
    }

    public float getPixelX() {
        return tileX * GameConstant.TILE_SIZE;
    }

    public float getPixelY() {
        return tileY * GameConstant.TILE_SIZE;
    }

    public boolean isAtTile(int x, int y) {
        return this.tileX == x && this.tileY == y;
    }
}
