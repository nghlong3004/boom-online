package vn.nghlong3004.boom.online.client.core.state;

import com.google.gson.Gson;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import raven.modal.ModalDialog;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.core.GamePanel;
import vn.nghlong3004.boom.online.client.core.manager.BombManager;
import vn.nghlong3004.boom.online.client.core.manager.BomberManager;
import vn.nghlong3004.boom.online.client.core.manager.ItemManager;
import vn.nghlong3004.boom.online.client.model.bomber.Direction;
import vn.nghlong3004.boom.online.client.model.game.*;
import vn.nghlong3004.boom.online.client.model.map.GameMap;
import vn.nghlong3004.boom.online.client.model.playing.PlayerInfo;
import vn.nghlong3004.boom.online.client.model.response.GameUpdate;
import vn.nghlong3004.boom.online.client.renderer.GameResultRenderer;
import vn.nghlong3004.boom.online.client.renderer.HudRenderer;
import vn.nghlong3004.boom.online.client.renderer.MapRenderer;
import vn.nghlong3004.boom.online.client.renderer.SpectatorDialogRenderer;
import vn.nghlong3004.boom.online.client.renderer.TimerRenderer;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.GameSession;
import vn.nghlong3004.boom.online.client.session.PlayingSession;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Slf4j
public class PlayingState implements GameState {

  private final MapRenderer mapRenderer;
  private final HudRenderer hudRenderer;
  private final GameResultRenderer resultRenderer;
  private final TimerRenderer timerRenderer;
  private final SpectatorDialogRenderer spectatorDialog;
  private final GamePanel gamePanel;
  private final Gson gson;

  private BomberManager bomberManager;
  private BombManager bombManager;
  private ItemManager itemManager;
  private boolean initialized;
  private GameResult gameResult;
  private boolean gameEnded;
  private boolean spectating;
  private boolean localPlayerDied;

  public PlayingState(GamePanel gamePanel, Gson gson) {
    this.gson = gson;
    this.gamePanel = gamePanel;
    this.mapRenderer = new MapRenderer();
    this.hudRenderer = new HudRenderer();
    this.resultRenderer = new GameResultRenderer();
    this.timerRenderer = new TimerRenderer();
    this.spectatorDialog = new SpectatorDialogRenderer();
    this.initialized = false;
    this.gameResult = null;
    this.gameEnded = false;
    this.spectating = false;
    this.localPlayerDied = false;
  }

  @Override
  public void next(GameContext gameContext) {
    gameContext.changeState(GameStateType.PLAYING);
  }

  @Override
  public void previous(GameContext gameContext) {
    GameSession.getInstance().endGame();
    PlayingSession.getInstance().clear();
    bomberManager = null;
    bombManager = null;
    itemManager = null;
    initialized = false;
    gameResult = null;
    gameEnded = false;
    spectating = false;
    localPlayerDied = false;
    spectatorDialog.hide();
    resultRenderer.reset();

    gameContext.changeState(GameStateType.START);
  }

  @Override
  public void update() {
    gamePanel.ensureFocus();
    if (!initialized) {
      initialize();
    }

    if (gameEnded) {
      resultRenderer.update();
      return;
    }

    if (spectatorDialog.isVisible()) {
      spectatorDialog.update();
    }

    GameSession.getInstance().updateTimer();

    if (bomberManager != null) {
      bomberManager.update();
    }

    if (bombManager != null) {
      bombManager.update();
      checkExplosionCollisions();
    }

    if (itemManager != null) {
      itemManager.update();
      if (bomberManager != null && bomberManager.getLocalBomber() != null) {
        boolean isOnline = GameSession.getInstance().isOnline();
        itemManager.checkCollision(bomberManager.getLocalBomber(), isOnline);
      }
    }

    checkLocalPlayerDeath();
    checkGameEnd();
  }

  private void initialize() {
    initialized = true;
    closeAllModals();
    initializeManagers();
    initializeOnlineGame();
    log.info("Game started - Map: {}", getGameMap().getMapType().getName());
  }

  private void initializeManagers() {
    GameMap gameMap = getGameMap();
    List<PlayerInfo> players = getPlayers();
    String localUserId = getCurrentUsername();

    bomberManager = new BomberManager(gameMap);
    bomberManager.initializeBombers(players, localUserId);

    itemManager = new ItemManager();
    itemManager.setOnItemCollectedNetwork(this::onItemCollectedNetwork);

    bombManager = new BombManager(gameMap);
    bombManager.setOnBrickDestroyed(this::onBrickDestroyed);
    bombManager.setOnExplosionAt(this::onExplosionAt);
  }

  private void onBrickDestroyed(int tileX, int tileY, int tileType) {
    if (itemManager == null) {
      return;
    }

    if (GameSession.getInstance().isOnline()) {
      GameSession.getInstance().sendBrickDestroyed(tileX, tileY, tileType);
    } else {
      itemManager.spawnItemAt(tileX, tileY, tileType);
    }
  }

  private void onItemCollectedNetwork(String itemId, int tileX, int tileY) {
    GameSession.getInstance().sendItemCollected(itemId, tileX, tileY);
  }

  private void onExplosionAt(int tileX, int tileY) {
    if (itemManager != null) {
      itemManager.destroyItemsAt(tileX, tileY);
    }
  }

  private void initializeOnlineGame() {
    GameSession gameSession = GameSession.getInstance();
    gameSession.setOnTimeExpired(this::handleTimeExpired);

    if (!ApplicationSession.getInstance().isOfflineMode()) {
      String roomId = PlayingSession.getInstance().getRoom().getId();
      gameSession.startOnlineGame(roomId, this::handleGameUpdate);
    } else {
      gameSession.startOfflineGame();
    }
  }

  private void handleTimeExpired() {
    log.info("Game time expired - determining winner");
    spectatorDialog.hide();
    determineWinnerOnTimeout();
  }

  private void checkLocalPlayerDeath() {
    if (gameEnded || localPlayerDied || bomberManager == null) {
      return;
    }

    if (!bomberManager.isLocalPlayerAlive()) {
      localPlayerDied = true;
      int aliveCount = bomberManager.getAliveCount();
      int totalPlayers = bomberManager.getBombers().size();

      log.info("Local player died - aliveCount: {}, totalPlayers: {}", aliveCount, totalPlayers);

      if (totalPlayers == 1 || aliveCount <= 1) {
        if (!GameSession.getInstance().isOnline()) {
          return;
        }
      }
      log.info("Showing spectator dialog - others still playing");
      GameSession gameSession = GameSession.getInstance();
      if (gameSession.getGameService() == null) {
        return;
      }
      gameSession.sendPlayerHit(UserSession.getInstance().getCurrentUser().getEmail());
      spectatorDialog.show();
    }
  }

  private void checkGameEnd() {
    if (gameEnded || bomberManager == null) {
      return;
    }

    if (GameSession.getInstance().isOnline()) {
      return;
    }

    int aliveCount = bomberManager.getAliveCount();
    int totalPlayers = bomberManager.getBombers().size();

    if (totalPlayers == 1) {
      if (!bomberManager.isLocalPlayerAlive()) {
        log.info("Single player mode - player died, triggering LOSE");
        spectatorDialog.hide();
        endGame(GameResult.LOSE);
      }
      return;
    }

    if (aliveCount <= 1) {
      spectatorDialog.hide();
      if (aliveCount == 0) {
        endGame(GameResult.DRAW);
      } else if (bomberManager.isLocalPlayerAlive()) {
        endGame(GameResult.WIN);
      } else {
        endGame(GameResult.LOSE);
      }
    }
  }

  private void determineWinnerOnTimeout() {
    if (gameEnded || bomberManager == null) {
      return;
    }

    int aliveCount = bomberManager.getAliveCount();

    if (aliveCount == 0) {
      endGame(GameResult.DRAW);
    } else if (aliveCount == 1) {
      if (bomberManager.isLocalPlayerAlive()) {
        endGame(GameResult.WIN);
      } else {
        endGame(GameResult.LOSE);
      }
    } else {
      if (bomberManager.isLocalPlayerAlive()) {
        endGame(GameResult.DRAW);
      } else {
        endGame(GameResult.LOSE);
      }
    }
  }

  private void endGame(GameResult result) {
    endGame(result, true);
  }

  private void endGame(GameResult result, boolean notifyServer) {
    if (gameEnded) {
      return;
    }

    this.gameResult = result;
    this.gameEnded = true;
    resultRenderer.reset();
    log.info("Game ended with result: {}", result);

    if (notifyServer && GameSession.getInstance().isOnline()) {
      notifyServerGameEnd(result);
    }
  }

  private void notifyServerGameEnd(GameResult result) {
    GameSession gameSession = GameSession.getInstance();
    if (gameSession.getGameService() == null) {
      return;
    }

    String winnerId = null;
    String reason = "game_ended";

    if (result == GameResult.WIN) {
      winnerId = getCurrentUsername();
      reason = "last_player_standing";
    } else if (result == GameResult.LOSE && bomberManager != null) {
      var lastAlive = bomberManager.getLastAlive();
      if (lastAlive != null) {
        winnerId = String.valueOf(lastAlive.getUserId());
        reason = "last_player_standing";
      }
    } else if (result == GameResult.DRAW) {
      reason = "draw";
    }

    gameSession.getGameService().sendGameEnd(winnerId, reason);
  }

  private void handleGameUpdate(GameUpdate update) {
    if (update == null || update.type() == null) {
      return;
    }

    String localUserId = getCurrentUsername();
    log.info(
        "update with action type :{}, updateId: {}, localID: {}",
        update.type(),
        update.playerId(),
        localUserId);
    if (update.playerId() != null
        && update.playerId().equals(localUserId)
        && update.type() != GameActionType.ITEM_COLLECTED) {
      return;
    }
    switch (update.type()) {
      case MOVE -> handleMoveUpdate(update);
      case PLACE_BOMB -> handlePlaceBombUpdate(update);
      case PLAYER_HIT -> handlePlayerHitUpdate(update);
      case PLAYER_DIED -> handlePlayerDiedUpdate(update);
      case ITEM_SPAWNED -> handleItemSpawnedUpdate(update);
      case ITEM_COLLECTED -> handleItemCollectedUpdate(update);
      case GAME_END -> handleGameEndUpdate(update);
      default -> log.debug("Unhandled game update type: {}", update.type());
    }
  }

  private void handleMoveUpdate(GameUpdate update) {
    if (bomberManager == null || update.data() == null) {
      return;
    }

    try {
      MoveData data = gson.fromJson(gson.toJsonTree(update.data()), MoveData.class);
      float x = data.x();
      float y = data.y();
      String directionStr = data.direction();
      Direction direction = Direction.valueOf(directionStr);

      Long userId = Long.parseLong(update.playerId());
      bomberManager.updateBomberPosition(userId, x, y, direction);
    } catch (Exception e) {
      log.error("Failed to handle move update", e);
    }
  }

  private void handlePlaceBombUpdate(GameUpdate update) {
    if (bombManager == null || update.data() == null) {
      return;
    }

    try {
      PlaceBombData data = gson.fromJson(gson.toJsonTree(update.data()), PlaceBombData.class);

      Long ownerId = Long.parseLong(update.playerId());

      bombManager.placeBombFromNetwork(data.tileX(), data.tileY(), data.power(), ownerId);
    } catch (Exception e) {
      log.error("Failed to handle place bomb update", e);
    }
  }

  private void handlePlayerHitUpdate(GameUpdate update) {
    if (bomberManager == null || update.data() == null) {
      return;
    }

    try {
      PlayerHitData data = gson.fromJson(gson.toJsonTree(update.data()), PlayerHitData.class);
      Long userId = Long.parseLong(data.playerId());
      bomberManager.handleBomberDeath(userId);
    } catch (Exception e) {
      log.error("Failed to handle player hit update", e);
    }
  }

  private void handlePlayerDiedUpdate(GameUpdate update) {
    if (bomberManager == null) {
      return;
    }

    try {
      Long userId = Long.parseLong(update.playerId());
      bomberManager.handleBomberDeath(userId);
    } catch (Exception e) {
      log.error("Failed to handle player died update", e);
    }
  }

  private void handleItemSpawnedUpdate(GameUpdate update) {
    if (itemManager == null || update.data() == null) {
      return;
    }

    try {
      ItemSpawnedData data = gson.fromJson(gson.toJsonTree(update.data()), ItemSpawnedData.class);
      String itemId = data.itemId();
      int tileX = data.tileX();
      int tileY = data.tileY();
      int itemType = data.itemType();

      itemManager.spawnItemFromNetwork(itemId, tileX, tileY, itemType);
      log.debug("Item spawned from server: {} at ({}, {}) type={}", itemId, tileX, tileY, itemType);
    } catch (Exception e) {
      log.error("Failed to handle item spawned update", e);
    }
  }

  private void handleItemCollectedUpdate(GameUpdate update) {
    if (itemManager == null || update.data() == null) {
      log.warn("handleItemCollectedUpdate: itemManager={} data={}", itemManager, update.data());
      return;
    }

    try {
      ItemCollectedData data = gson.fromJson(gson.toJsonTree(update.data()), ItemCollectedData.class);
      String collectorId = update.playerId();

      String localUserId = getCurrentUsername();
      log.info(
          "ITEM_COLLECTED received: itemId={}, collectorId={}, localUserId={}",
          data.itemId(),
          collectorId,
          localUserId);

      if (collectorId != null && collectorId.equals(localUserId)) {
        log.info("This is MY item collection - applying effect");
        if (bomberManager != null && bomberManager.getLocalBomber() != null) {
          itemManager.applyItemEffect(data.itemId(), bomberManager.getLocalBomber());
        } else {
          log.warn("bomberManager={} localBomber={}", bomberManager, null);
        }
      } else {
        log.info("Other player collected item - just removing from map");
        itemManager.collectItemFromNetwork(data.itemId(), data.tileX(), data.tileY());
      }
    } catch (Exception e) {
      log.error("Failed to handle item collected update", e);
    }
  }

  private void handleGameEndUpdate(GameUpdate update) {
    if (gameEnded) {
      return;
    }

    try {
      GameEndData data = gson.fromJson(gson.toJsonTree(update.data()), GameEndData.class);

      String localUserId = getCurrentUsername();
      GameResult result;
      log.info("winner info: {}", data.winnerId());
      if (data.winnerId() == null || data.winnerId().isEmpty()) {
        result = GameResult.DRAW;
      } else if (data.winnerId().equals(localUserId)) {
        result = GameResult.WIN;
      } else {
        result = GameResult.LOSE;
      }

      log.info(
          "Game ended from server - winner: {}, reason: {}, result: {}",
          data.winnerId(),
          data.reason(),
          result);
      spectatorDialog.hide();
      endGame(result, false);
    } catch (Exception e) {
      log.error("Failed to handle game end update", e);
      spectatorDialog.hide();
      endGame(GameResult.DRAW, false);
    }
  }

  private void checkExplosionCollisions() {
    if (bomberManager == null || bombManager == null) {
      return;
    }

    bomberManager
        .getBombers()
        .forEach(
            bomber -> {
              if (bomber.canBeHit()
                  && bombManager.isExplosionAt(bomber.getTileX(), bomber.getTileY())) {
                bomber.die();
                log.info(
                    "Bomber {} hit by explosion, lives: {}, alive: {}",
                    bomber.getDisplayName(),
                    bomber.getLives(),
                    bomber.isAlive());
              }
            });
  }

  private String getCurrentUsername() {
    if (UserSession.getInstance().getCurrentUser() != null) {
      return String.valueOf(UserSession.getInstance().getCurrentUser().getId());
    }
    return "offline-player";
  }

  @Override
  public void render(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    renderMap(g2d);
    renderItems(g2d);
    renderBombs(g2d);
    renderBombers(g2d);
    renderHud(g);
    if (gameEnded && gameResult != null) {
      resultRenderer.render(g2d, gameResult);
    } else {
      renderTimer(g2d);
    }

    if (spectatorDialog.isVisible()) {
      spectatorDialog.render(g2d, gamePanel.getWidth(), gamePanel.getHeight());
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (spectatorDialog.isVisible()) {
      handleSpectatorDialogInput(e);
      return;
    }

    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      previous(GameContext.getInstance());
      return;
    }

    if (gameEnded) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
        log.info("Player pressed Enter/Space to exit from result screen");
        previous(GameContext.getInstance());
      }
      return;
    }

    if (spectating
        && (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)) {
      spectatorDialog.show();
      return;
    }

    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      handlePlaceBomb();
      return;
    }

    if (bomberManager != null) {
      bomberManager.keyPressed(e);
    }
  }

  private void handleSpectatorDialogInput(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT, KeyEvent.VK_A -> spectatorDialog.selectLeft();
      case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> spectatorDialog.selectRight();
      case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> {
        if (spectatorDialog.getSelectedButton() == 0) {
          spectatorDialog.hide();
          spectating = true;
          log.info("Player chose to spectate");
        } else {
          log.info("Player chose to exit to lobby");
          previous(GameContext.getInstance());
        }
      }
      case KeyEvent.VK_ESCAPE -> previous(GameContext.getInstance());
    }
  }

  private void handlePlaceBomb() {
    if (bomberManager != null && bombManager != null) {
      var localBomber = bomberManager.getLocalBomber();
      if (localBomber != null && localBomber.isAlive()) {
        bombManager.placeBomb(localBomber);
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (bomberManager != null) {
      bomberManager.keyReleased(e);
    }
  }

  private void renderMap(Graphics2D g2d) {
    GameMap gameMap = getGameMap();
    if (gameMap != null) {
      mapRenderer.render(g2d, gameMap);
    }
  }

  private void renderItems(Graphics2D g2d) {
    if (itemManager != null) {
      itemManager.render(g2d);
    }
  }

  private void renderBombs(Graphics2D g2d) {
    if (bombManager != null) {
      bombManager.render(g2d);
    }
  }

  private void renderBombers(Graphics2D g2d) {
    if (bomberManager != null) {
      bomberManager.render(g2d);
    }
  }

  private void renderTimer(Graphics2D g2d) {
    GameSession gameSession = GameSession.getInstance();
    String timeText = gameSession.getFormattedTime();
    int remainingSeconds = gameSession.getRemainingSeconds();
    timerRenderer.render(g2d, timeText, remainingSeconds);
  }

  private void renderHud(Graphics g) {
    List<PlayerInfo> players = getPlayers();
    hudRenderer.render(g, players);
  }

  private GameMap getGameMap() {
    return PlayingSession.getInstance().getGameMap();
  }

  private List<PlayerInfo> getPlayers() {
    return PlayingSession.getInstance().getPlayers();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (gameEnded && gameResult != null) {
      if (resultRenderer.handleMouseClick(e.getX(), e.getY())) {
        log.info("Player clicked to exit to lobby from result screen");
        previous(GameContext.getInstance());
        return;
      }
    }

    if (spectatorDialog.isVisible()) {
      int clicked = spectatorDialog.handleMouseClick(e.getX(), e.getY());
      if (clicked == 0) {
        spectatorDialog.hide();
        spectating = true;
        log.info("Player clicked to spectate");
      } else if (clicked == 1) {
        log.info("Player clicked to exit to lobby");
        previous(GameContext.getInstance());
      }
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (gameEnded && gameResult != null) {
      resultRenderer.handleMouseMove(e.getX(), e.getY());
    }
    if (spectatorDialog.isVisible()) {
      spectatorDialog.handleMouseMove(e.getX(), e.getY());
    }
  }

  private void closeAllModals() {
    String startId = ApplicationSession.getInstance().getStartId();
    if (ModalDialog.isIdExist(startId)) {
      ModalDialog.closeModal(startId);
    }
  }
}
