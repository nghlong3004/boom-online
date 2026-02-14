package vn.nghlong3004.boom.online.client.model.item;

import vn.nghlong3004.boom.online.client.model.bomber.Bomber;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
@FunctionalInterface
public interface ItemEffect {
    void apply(Bomber bomber);
}
