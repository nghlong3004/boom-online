package vn.nghlong3004.boom.online.client.core.manager;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Setter;
import vn.nghlong3004.boom.online.client.model.bomb.Bomb;
import vn.nghlong3004.boom.online.client.model.bomb.BombState;
import vn.nghlong3004.boom.online.client.model.bomb.Explosion;
import vn.nghlong3004.boom.online.client.model.bomber.Bomber;
import vn.nghlong3004.boom.online.client.model.bomber.Direction;
import vn.nghlong3004.boom.online.client.model.map.GameMap;
import vn.nghlong3004.boom.online.client.renderer.BombRenderer;
import vn.nghlong3004.boom.online.client.renderer.ExplosionRenderer;
import vn.nghlong3004.boom.online.client.session.GameSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class BombManager {

  private static final int DEFAULT_MAX_BOMBS = 1;
  private static final int DEFAULT_POWER = 1;

  private final List<Bomb> bombs;
  private final List<Explosion> explosions;
  private final Map<Explosion, ExplosionRenderer> explosionRenderers;
  private final BombRenderer bombRenderer;
  private final GameMap gameMap;

  @Setter private int maxBombs;

  public BombManager(GameMap gameMap) {
    this.bombs = new ArrayList<>();
    this.explosions = new ArrayList<>();
    this.explosionRenderers = new HashMap<>();
    this.bombRenderer = new BombRenderer();
    this.gameMap = gameMap;
    this.maxBombs = DEFAULT_MAX_BOMBS;
  }

  public boolean placeBomb(Bomber bomber) {
    int tileX = bomber.getTileX();
    int tileY = bomber.getTileY();

    if (hasBombAt(tileX, tileY)) {
      return false;
    }

    long bomberBombCount =
        bombs.stream().filter(b -> b.getOwnerId().equals(bomber.getUserId())).count();

    if (bomberBombCount >= maxBombs) {
      return false;
    }

    Bomb bomb =
        Bomb.builder()
            .tileX(tileX)
            .tileY(tileY)
            .power(DEFAULT_POWER)
            .ownerId(bomber.getUserId())
            .build();

    bombs.add(bomb);

    GameSession.getInstance().sendPlaceBomb(tileX, tileY, DEFAULT_POWER);

    return true;
  }

  public void placeBombFromNetwork(int tileX, int tileY, int power, Long ownerId) {
    if (hasBombAt(tileX, tileY)) {
      return;
    }

    Bomb bomb = Bomb.builder().tileX(tileX).tileY(tileY).power(power).ownerId(ownerId).build();

    bombs.add(bomb);
  }

  public void update() {
    bombRenderer.update();
    updateBombs();
    updateExplosions();
  }

  private void updateBombs() {
    Iterator<Bomb> iterator = bombs.iterator();
    while (iterator.hasNext()) {
      Bomb bomb = iterator.next();
      bomb.update();

      if (bomb.shouldExplode()) {
        createExplosion(bomb);
        iterator.remove();
      }
    }
  }

  private void createExplosion(Bomb bomb) {
    Explosion explosion =
        new Explosion(
            bomb.getTileX(), bomb.getTileY(), bomb.getPower(), this::calculateExplosionRange);
    explosions.add(explosion);
    explosionRenderers.put(explosion, new ExplosionRenderer());

    triggerChainExplosions(explosion);
  }

  private int calculateExplosionRange(int startX, int startY, Direction direction, int maxPower) {
    for (int i = 1; i <= maxPower; i++) {
      int checkX = startX + (int) (direction.getDeltaX() * i);
      int checkY = startY + (int) (direction.getDeltaY() * i);

      if (!gameMap.isInBounds(checkY, checkX)) {
        return i - 1;
      }

      int tileValue = gameMap.getTile(checkY, checkX);

      if (tileValue == 0 || tileValue == 2) {
        return i - 1;
      }

      if (tileValue == 3) {
        gameMap.destroyBrick(checkY, checkX);
        return i;
      }
    }
    return maxPower;
  }

  private void triggerChainExplosions(Explosion explosion) {
    for (Bomb bomb : bombs) {
      if (explosion.containsTile(bomb.getTileX(), bomb.getTileY())) {
        bomb.setState(BombState.EXPLODING);
      }
    }
  }

  private void updateExplosions() {
    Iterator<Explosion> iterator = explosions.iterator();
    while (iterator.hasNext()) {
      Explosion explosion = iterator.next();
      ExplosionRenderer renderer = explosionRenderers.get(explosion);

      explosion.update();
      if (renderer != null) {
        renderer.update(explosion);
      }

      if (explosion.isDone()) {
        explosionRenderers.remove(explosion);
        iterator.remove();
      }
    }
  }

  public void render(Graphics2D g2d) {
    for (Bomb bomb : bombs) {
      bombRenderer.render(g2d, bomb);
    }

    for (Explosion explosion : explosions) {
      ExplosionRenderer renderer = explosionRenderers.get(explosion);
      if (renderer != null) {
        renderer.render(g2d, explosion);
      }
    }
  }

  public boolean hasBombAt(int tileX, int tileY) {
    return bombs.stream().anyMatch(b -> b.getTileX() == tileX && b.getTileY() == tileY);
  }

  public boolean isExplosionAt(int tileX, int tileY) {
    return explosions.stream().anyMatch(e -> e.containsTile(tileX, tileY));
  }

  public List<Explosion> getActiveExplosions() {
    return new ArrayList<>(explosions);
  }
}
