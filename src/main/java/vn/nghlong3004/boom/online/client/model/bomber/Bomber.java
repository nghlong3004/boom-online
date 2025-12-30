package vn.nghlong3004.boom.online.client.model.bomber;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.nghlong3004.boom.online.client.constant.GameConstant;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Getter
@Setter
@Builder
public class Bomber {

  private static final float DEFAULT_SPEED = 0.6f;
  private static final int DEFAULT_LIVES = 1;
  private static final int INVINCIBILITY_FRAMES = 120; // 2 seconds at 60 FPS

  private final int playerIndex;
  private final Long userId;
  private final String displayName;
  private final BomberType bomberType;

  private float x;
  private float y;
  @Builder.Default private float speed = DEFAULT_SPEED;
  @Builder.Default private Direction direction = Direction.DOWN;

  @Builder.Default private BomberState state = BomberState.IDLE;
  @Builder.Default private int lives = DEFAULT_LIVES;

  @Builder.Default private boolean alive = true;
  @Builder.Default private int invincibilityTicks = 0;

  public void move(Direction newDirection) {
    this.direction = newDirection;
    this.state = BomberState.WALKING;
    this.x += newDirection.getDeltaX() * speed;
    this.y += newDirection.getDeltaY() * speed;
  }

  public void stop() {
    this.state = BomberState.IDLE;
  }

  public void update() {
    if (invincibilityTicks > 0) {
      invincibilityTicks--;
    }
  }

  public boolean canBeHit() {
    return alive && invincibilityTicks <= 0;
  }

  public boolean isInvincible() {
    return invincibilityTicks > 0;
  }

  public void die() {
    if (!canBeHit()) {
      return;
    }

    this.lives--;
    if (this.lives <= 0) {
      this.alive = false;
      this.state = BomberState.DEAD;
    } else {
      this.state = BomberState.DYING;
      this.invincibilityTicks = INVINCIBILITY_FRAMES;
    }
  }

  public void respawn(float spawnX, float spawnY) {
    this.x = spawnX;
    this.y = spawnY;
    this.state = BomberState.IDLE;
    this.direction = Direction.DOWN;
  }

  public int getTileX() {
    return (int) ((x + GameConstant.TILE_SIZE / 2.0f) / GameConstant.TILE_SIZE);
  }

  public int getTileY() {
    return (int) ((y + GameConstant.TILE_SIZE / 2.0f) / GameConstant.TILE_SIZE);
  }

  public float getCenterX() {
    return x + GameConstant.TILE_SIZE / 2.0f;
  }

  public float getCenterY() {
    return y + GameConstant.TILE_SIZE / 2.0f;
  }
}
