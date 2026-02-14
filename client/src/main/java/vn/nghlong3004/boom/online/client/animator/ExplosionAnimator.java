package vn.nghlong3004.boom.online.client.animator;

import java.awt.image.BufferedImage;
import vn.nghlong3004.boom.online.client.assets.BombAssets;
import vn.nghlong3004.boom.online.client.model.bomb.Explosion;
import vn.nghlong3004.boom.online.client.model.bomb.ExplosionState;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class ExplosionAnimator {

  private static final int ANIMATION_SPEED = 6;

  private final BufferedImage[] explosionFrames;
  private int animationTick;
  private int currentFrame;

  public ExplosionAnimator() {
    this.explosionFrames = BombAssets.getInstance().getExplosionFrames();
    this.animationTick = 0;
    this.currentFrame = 0;
  }

  public void update(Explosion explosion) {
    if (explosion.getState() == ExplosionState.DONE) {
      return;
    }

    animationTick++;
    if (animationTick >= ANIMATION_SPEED) {
      animationTick = 0;
      if (currentFrame < explosionFrames.length - 1) {
        currentFrame++;
      }
    }
  }

  public BufferedImage getCurrentFrame() {
    if (explosionFrames == null || explosionFrames.length == 0) {
      return null;
    }
    return explosionFrames[Math.min(currentFrame, explosionFrames.length - 1)];
  }

  public void reset() {
    animationTick = 0;
    currentFrame = 0;
  }
}
