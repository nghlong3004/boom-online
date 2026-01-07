package vn.nghlong3004.boom.online.client.model.item;

import java.util.EnumMap;
import java.util.Map;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
public final class ItemEffectRegistry {

    private static final Map<ItemType, ItemEffect> EFFECTS = new EnumMap<>(ItemType.class);

    static {
        EFFECTS.put(ItemType.BOMB_UP, new BombUpEffect());
        EFFECTS.put(ItemType.FIRE_UP, new FireUpEffect());
        EFFECTS.put(ItemType.SPEED_UP, new SpeedUpEffect());
    }

    private ItemEffectRegistry() {
    }

    public static ItemEffect getEffect(ItemType type) {
        return EFFECTS.get(type);
    }
}
