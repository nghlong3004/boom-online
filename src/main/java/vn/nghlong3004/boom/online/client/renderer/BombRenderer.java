package vn.nghlong3004.boom.online.client.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import vn.nghlong3004.boom.online.client.animator.BombAnimator;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.model.bomb.Bomb;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class BombRenderer {

  private static final float RENDER_SCALE = 1.2f;

  private final BombAnimator animator;

  public BombRenderer() {
    this.animator = new BombAnimator();
  }

  public void update() {
    animator.update();
  }

  public void render(Graphics2D g2d, Bomb bomb) {
    BufferedImage frame = animator.getCurrentFrame(bomb);
    if (frame == null) {
      return;
    }

    int renderSize = (int) (GameConstant.TILE_SIZE * RENDER_SCALE);
    int offsetX = (GameConstant.TILE_SIZE - renderSize) / 2;
    int offsetY = (GameConstant.TILE_SIZE - renderSize) / 2;

    int x = (int) bomb.getPixelX() + offsetX;
    int y = (int) bomb.getPixelY() + offsetY;

    g2d.drawImage(frame, x, y, renderSize, renderSize, null);
  }
}
