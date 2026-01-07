package vn.nghlong3004.boom.online.client.model.item;

import vn.nghlong3004.boom.online.client.model.bomber.Bomber;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
public class BombUpEffect implements ItemEffect {

    private static final int BOMB_INCREMENT = 1;
    private static final int MAX_BOMBS = 8;

    @Override
    public void apply(Bomber bomber) {
        int currentBombs = bomber.getMaxBombs();
        if (currentBombs < MAX_BOMBS) {
            bomber.setMaxBombs(currentBombs + BOMB_INCREMENT);
        }
    }
}
