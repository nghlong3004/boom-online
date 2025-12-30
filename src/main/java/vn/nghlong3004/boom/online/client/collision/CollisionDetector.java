package vn.nghlong3004.boom.online.client.collision;

import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.model.bomber.Bomber;
import vn.nghlong3004.boom.online.client.model.bomber.Direction;
import vn.nghlong3004.boom.online.client.model.map.GameMap;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
public class CollisionDetector {

  private static final float COLLISION_MARGIN = 4.0f;
  private static final float HITBOX_RATIO = 0.6f;

  private final GameMap gameMap;

  public CollisionDetector(GameMap gameMap) {
    this.gameMap = gameMap;
  }

  public boolean canMove(Bomber bomber, Direction direction) {
    float nextX = bomber.getX() + direction.getDeltaX() * bomber.getSpeed();
    float nextY = bomber.getY() + direction.getDeltaY() * bomber.getSpeed();

    return !hasCollision(nextX, nextY);
  }

  public boolean hasCollision(float x, float y) {
    float hitboxSize = GameConstant.TILE_SIZE * HITBOX_RATIO;
    float offsetX = (GameConstant.TILE_SIZE - hitboxSize) / 2;
    float offsetY = GameConstant.TILE_SIZE - hitboxSize;

    float left = x + offsetX + COLLISION_MARGIN;
    float right = x + offsetX + hitboxSize - COLLISION_MARGIN;
    float top = y + offsetY + COLLISION_MARGIN;
    float bottom = y + offsetY + hitboxSize - COLLISION_MARGIN;

    return isBlocked(left, top)
        || isBlocked(right, top)
        || isBlocked(left, bottom)
        || isBlocked(right, bottom);
  }

  private boolean isBlocked(float pixelX, float pixelY) {
    int tileCol = (int) (pixelX / GameConstant.TILE_SIZE);
    int tileRow = (int) (pixelY / GameConstant.TILE_SIZE);

    return !gameMap.isWalkable(tileRow, tileCol);
  }

  public void updateGameMap(GameMap newGameMap) {}
}
