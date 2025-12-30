package vn.nghlong3004.boom.online.client.animator;

import java.awt.image.BufferedImage;
import vn.nghlong3004.boom.online.client.assets.BombAssets;
import vn.nghlong3004.boom.online.client.model.bomb.Bomb;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class BombAnimator {

  private static final int ANIMATION_SPEED = 15;

  private final BufferedImage[] bombFrames;
  private int animationTick;
  private int currentFrame;

  public BombAnimator() {
    this.bombFrames = BombAssets.getInstance().getBombFrames();
    this.animationTick = 0;
    this.currentFrame = 0;
  }

  public void update() {
    animationTick++;
    if (animationTick >= ANIMATION_SPEED) {
      animationTick = 0;
      currentFrame = (currentFrame + 1) % bombFrames.length;
    }
  }

  public BufferedImage getCurrentFrame(Bomb bomb) {
    if (bombFrames == null || bombFrames.length == 0) {
      return null;
    }
    return bombFrames[currentFrame];
  }

  public void reset() {
    animationTick = 0;
    currentFrame = 0;
  }
}
