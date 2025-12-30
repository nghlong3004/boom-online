package vn.nghlong3004.boom.online.client.renderer;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import vn.nghlong3004.boom.online.client.animator.ExplosionAnimator;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.model.bomb.Explosion;
import vn.nghlong3004.boom.online.client.model.bomb.Explosion.ExplosionTile;
import vn.nghlong3004.boom.online.client.model.bomb.ExplosionState;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class ExplosionRenderer {

  private static final float RENDER_SCALE = 1.2f;

  private final ExplosionAnimator animator;

  public ExplosionRenderer() {
    this.animator = new ExplosionAnimator();
  }

  public void update(Explosion explosion) {
    animator.update(explosion);
  }

  public void render(Graphics2D g2d, Explosion explosion) {
    BufferedImage frame = animator.getCurrentFrame();
    if (frame == null) {
      return;
    }

    Composite originalComposite = g2d.getComposite();

    if (explosion.getState() == ExplosionState.FADING) {
      float alpha = explosion.getDurationTicks() / 30.0f;
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.1f, alpha)));
    }

    for (ExplosionTile tile : explosion.getTiles()) {
      renderTile(g2d, tile, frame);
    }

    g2d.setComposite(originalComposite);
  }

  private void renderTile(Graphics2D g2d, ExplosionTile tile, BufferedImage frame) {
    int renderSize = (int) (GameConstant.TILE_SIZE * RENDER_SCALE);
    int offset = (GameConstant.TILE_SIZE - renderSize) / 2;

    int x = (int) tile.getPixelX() + offset;
    int y = (int) tile.getPixelY() + offset;

    g2d.drawImage(frame, x, y, renderSize, renderSize, null);
  }

  public void reset() {
    animator.reset();
  }
}
