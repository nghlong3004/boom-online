package vn.nghlong3004.boom.online.client.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import vn.nghlong3004.boom.online.client.animator.ItemAnimator;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.model.item.Item;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
public class ItemRenderer {

    private static final float RENDER_SCALE = 0.6f;

    private final Map<Item, ItemAnimator> animators;

    public ItemRenderer() {
        this.animators = new HashMap<>();
    }

    public void addItem(Item item) {
        animators.put(item, new ItemAnimator());
    }

    public void removeItem(Item item) {
        animators.remove(item);
    }

    public void update() {
        animators.forEach((item, animator) -> animator.update(item));
    }

    public void render(Graphics2D g2d, Item item) {
        ItemAnimator animator = animators.get(item);
        if (animator == null) {
            return;
        }

        BufferedImage frame = animator.getCurrentFrame(item);
        if (frame == null) {
            return;
        }

        int renderWidth = (int) (GameConstant.TILE_SIZE * RENDER_SCALE);
        int renderHeight = (int) (frame.getHeight() * RENDER_SCALE * GameConstant.TILE_SIZE / frame.getWidth());

        int offsetX = (GameConstant.TILE_SIZE - renderWidth) / 2;
        int offsetY = GameConstant.TILE_SIZE - renderHeight;

        int x = (int) item.getPixelX() + offsetX;
        int y = (int) item.getPixelY() + offsetY;

        g2d.drawImage(frame, x, y, renderWidth, renderHeight, null);
    }

    public void clear() {
        animators.clear();
    }
}
