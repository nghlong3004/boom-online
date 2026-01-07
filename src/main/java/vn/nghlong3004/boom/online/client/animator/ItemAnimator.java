package vn.nghlong3004.boom.online.client.animator;

import java.awt.image.BufferedImage;
import vn.nghlong3004.boom.online.client.assets.ObjectAssets;
import vn.nghlong3004.boom.online.client.model.item.Item;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
public class ItemAnimator {

    private static final int ANIMATION_SPEED = 20;

    private final BufferedImage[][] itemFrames;
    private int animationTick;
    private int currentFrame;

    public ItemAnimator() {
        this.itemFrames = ObjectAssets.getInstance().getItemAssets();
        this.animationTick = 0;
        this.currentFrame = 0;
    }

    public void update(Item item) {
        animationTick++;
        if (animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            currentFrame = (currentFrame + 1) % getFrameCount(item);
        }
    }

    public BufferedImage getCurrentFrame(Item item) {
        if (itemFrames == null) {
            return null;
        }

        int typeIndex = item.getType().spriteIndex;
        if (typeIndex < 0 || typeIndex >= itemFrames.length) {
            return null;
        }

        BufferedImage[] frames = itemFrames[typeIndex];
        if (frames == null || frames.length == 0) {
            return null;
        }

        return frames[currentFrame % frames.length];
    }

    private int getFrameCount(Item item) {
        int typeIndex = item.getType().spriteIndex;
        if (itemFrames == null || typeIndex < 0 || typeIndex >= itemFrames.length) {
            return 1;
        }
        BufferedImage[] frames = itemFrames[typeIndex];
        return frames != null ? frames.length : 1;
    }

    public void reset() {
        animationTick = 0;
        currentFrame = 0;
    }
}
