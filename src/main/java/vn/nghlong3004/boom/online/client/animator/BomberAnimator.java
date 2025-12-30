package vn.nghlong3004.boom.online.client.animator;

import java.awt.image.BufferedImage;
import vn.nghlong3004.boom.online.client.assets.BomberAssets;
import vn.nghlong3004.boom.online.client.model.bomber.Bomber;
import vn.nghlong3004.boom.online.client.model.bomber.BomberState;
import vn.nghlong3004.boom.online.client.model.bomber.Direction;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
public class BomberAnimator {

  private static final int ANIMATION_FRAMES = 5;
  private static final int DEATH_FRAMES = 4;
  private static final int ANIMATION_SPEED = 15;
  private static final int DEATH_ANIMATION_SPEED = 15;

  private final BufferedImage[][] sprites;
  private final BufferedImage[] deathSprites;

  private int animationTick;
  private int animationFrame;
  private int deathTick;
  private int deathFrame;

  public BomberAnimator(int bomberTypeId) {
    BomberAssets assets = BomberAssets.getInstance();
    int totalBomberTypes = assets.getBomberAssets().size();
    int safeIndex = Math.floorMod(bomberTypeId, totalBomberTypes);
    this.sprites = assets.getBomberAssets().get(safeIndex);
    this.deathSprites = assets.getBomberDeathAssets();
    this.animationTick = 0;
    this.animationFrame = 0;
    this.deathTick = 0;
    this.deathFrame = 0;
  }

  public void update(Bomber bomber) {
    if (bomber.getState() == BomberState.DYING || bomber.getState() == BomberState.DEAD) {
      updateDeathAnimation();
    } else if (bomber.getState() == BomberState.WALKING) {
      updateWalkAnimation();
    } else {
      resetAnimation();
    }
  }

  public BufferedImage getCurrentFrame(Bomber bomber) {
    if (bomber.getState() == BomberState.DYING || bomber.getState() == BomberState.DEAD) {
      return getDeathFrame();
    }
    return getWalkFrame(bomber.getDirection());
  }

  private void updateWalkAnimation() {
    animationTick++;
    if (animationTick >= ANIMATION_SPEED) {
      animationTick = 0;
      animationFrame = (animationFrame + 1) % ANIMATION_FRAMES;
    }
  }

  private void updateDeathAnimation() {
    deathTick++;
    if (deathTick >= DEATH_ANIMATION_SPEED) {
      deathTick = 0;
      if (deathFrame < DEATH_FRAMES - 1) {
        deathFrame++;
      }
    }
  }

  private void resetAnimation() {
    animationTick = 0;
    animationFrame = 2;
  }

  private BufferedImage getWalkFrame(Direction direction) {
    int row = direction.getAnimationRow();
    return sprites[row][animationFrame];
  }

  private BufferedImage getDeathFrame() {
    return deathSprites[deathFrame];
  }

  public void resetDeathAnimation() {
    deathTick = 0;
    deathFrame = 0;
  }

  public boolean isDeathAnimationComplete() {
    return deathFrame >= DEATH_FRAMES - 1;
  }
}
