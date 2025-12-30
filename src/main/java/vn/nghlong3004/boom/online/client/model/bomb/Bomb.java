package vn.nghlong3004.boom.online.client.model.bomb;

import lombok.Getter;
import lombok.Setter;
import vn.nghlong3004.boom.online.client.constant.GameConstant;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Getter
public class Bomb {

  private static final int DEFAULT_TIMER_TICKS = 60 * 5;
  private static final int DEFAULT_POWER = 1;

  private final int tileX;
  private final int tileY;
  private final int power;
  private final Long ownerId;

  @Setter private BombState state;
  private int timerTicks;

  private Bomb(Builder builder) {
    this.tileX = builder.tileX;
    this.tileY = builder.tileY;
    this.power = builder.power;
    this.ownerId = builder.ownerId;
    this.state = BombState.TICKING;
    this.timerTicks = DEFAULT_TIMER_TICKS;
  }

  public void update() {
    if (state == BombState.TICKING) {
      timerTicks--;
      if (timerTicks <= 0) {
        state = BombState.EXPLODING;
      }
    }
  }

  public boolean shouldExplode() {
    return state == BombState.EXPLODING;
  }

  public boolean isDone() {
    return state == BombState.DONE;
  }

  public float getPixelX() {
    return tileX * GameConstant.TILE_SIZE;
  }

  public float getPixelY() {
    return tileY * GameConstant.TILE_SIZE;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private int tileX;
    private int tileY;
    private int power = DEFAULT_POWER;
    private Long ownerId;

    public Builder tileX(int tileX) {
      this.tileX = tileX;
      return this;
    }

    public Builder tileY(int tileY) {
      this.tileY = tileY;
      return this;
    }

    public Builder power(int power) {
      this.power = power;
      return this;
    }

    public Builder ownerId(Long ownerId) {
      this.ownerId = ownerId;
      return this;
    }

    public Bomb build() {
      return new Bomb(this);
    }
  }
}
