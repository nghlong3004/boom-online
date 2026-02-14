package vn.nghlong3004.boom.online.client.model.item;

import vn.nghlong3004.boom.online.client.model.bomber.Bomber;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
public class SpeedUpEffect implements ItemEffect {

    private static final float SPEED_INCREMENT = 0.15f;
    private static final float MAX_SPEED = 1.8f;

    @Override
    public void apply(Bomber bomber) {
        float currentSpeed = bomber.getSpeed();
        if (currentSpeed < MAX_SPEED) {
            bomber.setSpeed(Math.min(currentSpeed + SPEED_INCREMENT, MAX_SPEED));
        }
    }
}
