package vn.nghlong3004.boom.online.client.core.manager;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import vn.nghlong3004.boom.online.client.collision.CollisionDetector;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.constant.PlayingConstant;
import vn.nghlong3004.boom.online.client.input.InputHandler;
import vn.nghlong3004.boom.online.client.model.bomber.Bomber;
import vn.nghlong3004.boom.online.client.model.bomber.BomberState;
import vn.nghlong3004.boom.online.client.model.bomber.Direction;
import vn.nghlong3004.boom.online.client.model.map.GameMap;
import vn.nghlong3004.boom.online.client.model.playing.PlayerInfo;
import vn.nghlong3004.boom.online.client.renderer.BomberRenderer;
import vn.nghlong3004.boom.online.client.session.GameSession;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
public class BomberManager {

  private static final float[][] SPAWN_POSITIONS = {
    {1, 1},
    {PlayingConstant.MAP_COLUMNS - 2, 1},
    {1, PlayingConstant.MAP_ROWS - 2},
    {PlayingConstant.MAP_COLUMNS - 2, PlayingConstant.MAP_ROWS - 2}
  };

  private static final int SYNC_INTERVAL = 3;

  private final List<Bomber> bombers;
  private final Map<Integer, BomberRenderer> renderers;
  private final InputHandler inputHandler;
  private final CollisionDetector collisionDetector;

  @Getter private Bomber localBomber;
  @Getter private int localPlayerIndex;

  private int syncTick;
  private float lastSyncX;
  private float lastSyncY;
  private Direction lastSyncDirection;

  public BomberManager(GameMap gameMap) {
    this.bombers = new ArrayList<>();
    this.renderers = new HashMap<>();
    this.inputHandler = new InputHandler();
    this.collisionDetector = new CollisionDetector(gameMap);
    this.localPlayerIndex = -1;
    this.syncTick = 0;
  }

  public void initializeBombers(List<PlayerInfo> players, String localUserId) {
    bombers.clear();
    renderers.clear();
    localBomber = null;
    localPlayerIndex = -1;

    for (int i = 0; i < players.size(); i++) {
      PlayerInfo player = players.get(i);
      float[] spawnPos = getSpawnPosition(i);

      Bomber bomber =
          Bomber.builder()
              .playerIndex(i)
              .userId(player.getUserId())
              .displayName(player.getDisplayName())
              .x(spawnPos[0] * GameConstant.TILE_SIZE)
              .y(spawnPos[1] * GameConstant.TILE_SIZE)
              .build();

      bombers.add(bomber);
      renderers.put(i, new BomberRenderer(player.getCharacterIndex()));

      boolean isLocalPlayer =
          localUserId.equals("offline-player")
              ? player.isHost()
              : player.getUserId().equals(UserSession.getInstance().getCurrentUser().getId());

      if (isLocalPlayer && localBomber == null) {
        localBomber = bomber;
        localPlayerIndex = i;
      }
    }
  }

  private float[] getSpawnPosition(int playerIndex) {
    if (playerIndex >= 0 && playerIndex < SPAWN_POSITIONS.length) {
      return SPAWN_POSITIONS[playerIndex];
    }
    return new float[] {1, 1};
  }

  public void update() {
    updateLocalBomber();
    updateAllBombers();
    syncLocalBomberPosition();
  }

  private void updateLocalBomber() {
    if (localBomber == null || !localBomber.isAlive()) {
      return;
    }

    localBomber.update();

    Direction direction = inputHandler.getCurrentDirection();

    if (direction != null) {
      localBomber.setDirection(direction);
      localBomber.setState(BomberState.WALKING);

      if (collisionDetector.canMove(localBomber, direction)) {
        localBomber.move(direction);
      }
    } else {
      localBomber.setState(BomberState.IDLE);
    }
  }

  private void syncLocalBomberPosition() {
    if (localBomber == null || !GameSession.getInstance().isOnline()) {
      return;
    }

    syncTick++;
    if (syncTick < SYNC_INTERVAL) {
      return;
    }
    syncTick = 0;

    boolean positionChanged =
        localBomber.getX() != lastSyncX
            || localBomber.getY() != lastSyncY
            || localBomber.getDirection() != lastSyncDirection;

    if (positionChanged) {
      lastSyncX = localBomber.getX();
      lastSyncY = localBomber.getY();
      lastSyncDirection = localBomber.getDirection();

      GameSession.getInstance()
          .sendMove(localBomber.getX(), localBomber.getY(), localBomber.getDirection().name());
    }
  }

  private void updateAllBombers() {
    for (int i = 0; i < bombers.size(); i++) {
      Bomber bomber = bombers.get(i);
      bomber.update();
      BomberRenderer renderer = renderers.get(i);
      if (renderer != null) {
        renderer.update(bomber);
      }
    }
  }

  public void render(Graphics2D g2d) {
    List<Integer> sortedIndices = new ArrayList<>();
    for (int i = 0; i < bombers.size(); i++) {
      sortedIndices.add(i);
    }
    sortedIndices.sort((i1, i2) -> Float.compare(bombers.get(i1).getY(), bombers.get(i2).getY()));

    for (int index : sortedIndices) {
      Bomber bomber = bombers.get(index);
      BomberRenderer renderer = renderers.get(index);
      if (renderer != null) {
        renderer.render(g2d, bomber);
      }
    }
  }

  public void keyPressed(KeyEvent e) {
    inputHandler.keyPressed(e.getKeyCode());
  }

  public void keyReleased(KeyEvent e) {
    inputHandler.keyReleased(e.getKeyCode());
  }

  public List<Bomber> getBombers() {
    return Collections.unmodifiableList(bombers);
  }

  public Bomber getBomberByUserId(Long userId) {
    return bombers.stream().filter(b -> b.getUserId().equals(userId)).findFirst().orElse(null);
  }

  public int getAliveCount() {
    return (int) bombers.stream().filter(Bomber::isAlive).count();
  }

  public Bomber getLastAlive() {
    return bombers.stream().filter(Bomber::isAlive).findFirst().orElse(null);
  }

  public boolean isLocalPlayerAlive() {
    return localBomber != null && localBomber.isAlive();
  }

  public void updateBomberPosition(Long userId, float x, float y, Direction direction) {
    Bomber bomber = getBomberByUserId(userId);
    if (bomber != null && bomber != localBomber) {
      bomber.setX(x);
      bomber.setY(y);
      bomber.setDirection(direction);
      bomber.setState(BomberState.WALKING);
    }
  }

  public void handleBomberDeath(Long userId) {
    Bomber bomber = getBomberByUserId(userId);
    if (bomber != null) {
      bomber.die();
    }
  }
}
