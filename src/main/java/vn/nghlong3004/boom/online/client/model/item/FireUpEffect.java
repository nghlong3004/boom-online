package vn.nghlong3004.boom.online.client.model.item;

import vn.nghlong3004.boom.online.client.model.bomber.Bomber;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
public class FireUpEffect implements ItemEffect {

    private static final int POWER_INCREMENT = 1;
    private static final int MAX_POWER = 8;

    @Override
    public void apply(Bomber bomber) {
        int currentPower = bomber.getBombPower();
        if (currentPower < MAX_POWER) {
            bomber.setBombPower(currentPower + POWER_INCREMENT);
        }
    }
}
