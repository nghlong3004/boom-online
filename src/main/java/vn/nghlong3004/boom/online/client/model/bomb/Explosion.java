package vn.nghlong3004.boom.online.client.model.bomb;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.model.bomber.Direction;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Getter
public class Explosion {

  private static final int EXPLOSION_DURATION = 60;

  private final int centerTileX;
  private final int centerTileY;
  private final List<ExplosionTile> tiles;

  @Setter
  private ExplosionState state;
  private int durationTicks;

  public Explosion(
      int centerTileX, int centerTileY, int power, ExplosionRangeCalculator rangeCalculator) {
    this.centerTileX = centerTileX;
    this.centerTileY = centerTileY;
    this.tiles = new ArrayList<>();
    this.state = ExplosionState.EXPANDING;
    this.durationTicks = EXPLOSION_DURATION;

    createExplosionTiles(power, rangeCalculator);
  }

  private void createExplosionTiles(int power, ExplosionRangeCalculator rangeCalculator) {
    tiles.add(new ExplosionTile(centerTileX, centerTileY, ExplosionTileType.CENTER));

    for (Direction direction : Direction.values()) {
      int range = rangeCalculator.calculateRange(centerTileX, centerTileY, direction, power);
      createDirectionTiles(direction, range);
    }
  }

  private void createDirectionTiles(Direction direction, int range) {
    for (int i = 1; i <= range; i++) {
      int tileX = centerTileX + (int) (direction.getDeltaX() * i);
      int tileY = centerTileY + (int) (direction.getDeltaY() * i);
      ExplosionTileType type = (i == range) ? getEndType(direction) : getMiddleType(direction);
      tiles.add(new ExplosionTile(tileX, tileY, type));
    }
  }

  private ExplosionTileType getMiddleType(Direction direction) {
    return switch (direction) {
      case UP, DOWN -> ExplosionTileType.VERTICAL;
      case LEFT, RIGHT -> ExplosionTileType.HORIZONTAL;
    };
  }

  private ExplosionTileType getEndType(Direction direction) {
    return switch (direction) {
      case UP -> ExplosionTileType.END_UP;
      case DOWN -> ExplosionTileType.END_DOWN;
      case LEFT -> ExplosionTileType.END_LEFT;
      case RIGHT -> ExplosionTileType.END_RIGHT;
    };
  }

  public void update() {
    if (state != ExplosionState.DONE) {
      durationTicks--;
      if (durationTicks <= EXPLOSION_DURATION / 2) {
        state = ExplosionState.FADING;
      }
      if (durationTicks <= 0) {
        state = ExplosionState.DONE;
      }
    }
  }

  public boolean isDone() {
    return state == ExplosionState.DONE;
  }

  public boolean containsTile(int tileX, int tileY) {
    return tiles.stream().anyMatch(t -> t.tileX() == tileX && t.tileY() == tileY);
  }

  public List<int[]> getAffectedTiles() {
    List<int[]> affectedTiles = new ArrayList<>();
    for (ExplosionTile tile : tiles) {
      affectedTiles.add(new int[] { tile.tileX(), tile.tileY() });
    }
    return affectedTiles;
  }

  @FunctionalInterface
  public interface ExplosionRangeCalculator {
    int calculateRange(int startX, int startY, Direction direction, int maxPower);
  }

  public record ExplosionTile(int tileX, int tileY, ExplosionTileType type) {
    public float getPixelX() {
      return tileX * GameConstant.TILE_SIZE;
    }

    public float getPixelY() {
      return tileY * GameConstant.TILE_SIZE;
    }
  }

  public enum ExplosionTileType {
    CENTER,
    HORIZONTAL,
    VERTICAL,
    END_UP,
    END_DOWN,
    END_LEFT,
    END_RIGHT
  }
}
