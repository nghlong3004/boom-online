package vn.nghlong3004.boom.online.client.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import lombok.Getter;
import vn.nghlong3004.boom.online.client.animator.BomberAnimator;
import vn.nghlong3004.boom.online.client.constant.BomberConstant;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.model.bomber.Bomber;
import vn.nghlong3004.boom.online.client.model.bomber.BomberState;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Getter
public class BomberRenderer {

  private static final int RENDER_WIDTH = (int) (BomberConstant.BOMBER_WIDTH_IMAGE * 0.9f);
  private static final int RENDER_HEIGHT = (int) (BomberConstant.BOMBER_HEIGHT_IMAGE * 0.9f);
  private static final int DEATH_RENDER_SIZE =
      (int) (BomberConstant.BOMBER_DEAD_SPRITE_SIZE * 0.6f);

  private final BomberAnimator animator;

  public BomberRenderer(int bomberTypeId) {
    this.animator = new BomberAnimator(bomberTypeId);
  }

  public void update(Bomber bomber) {
    animator.update(bomber);
  }

  public void render(Graphics2D g2d, Bomber bomber) {
    if (!bomber.isAlive() && animator.isDeathAnimationComplete()) {
      return;
    }

    BufferedImage frame = animator.getCurrentFrame(bomber);
    if (frame == null) {
      return;
    }

    int renderX = calculateRenderX(bomber);
    int renderY = calculateRenderY(bomber);
    int width = getRenderWidth(bomber);
    int height = getRenderHeight(bomber);

    g2d.drawImage(frame, renderX, renderY, width, height, null);
  }

  private int calculateRenderX(Bomber bomber) {
    int width = getRenderWidth(bomber);
    return (int) (bomber.getX() + (GameConstant.TILE_SIZE - width) / 2.0f);
  }

  private int calculateRenderY(Bomber bomber) {
    int height = getRenderHeight(bomber);
    return (int) (bomber.getY() + GameConstant.TILE_SIZE - height);
  }

  private int getRenderWidth(Bomber bomber) {
    if (bomber.getState() == BomberState.DYING || bomber.getState() == BomberState.DEAD) {
      return DEATH_RENDER_SIZE;
    }
    return RENDER_WIDTH;
  }

  private int getRenderHeight(Bomber bomber) {
    if (bomber.getState() == BomberState.DYING || bomber.getState() == BomberState.DEAD) {
      return DEATH_RENDER_SIZE;
    }
    return RENDER_HEIGHT;
  }
}
