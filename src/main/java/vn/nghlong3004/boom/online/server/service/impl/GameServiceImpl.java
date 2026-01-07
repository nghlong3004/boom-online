package vn.nghlong3004.boom.online.server.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import vn.nghlong3004.boom.online.server.exception.ErrorCode;
import vn.nghlong3004.boom.online.server.exception.ResourceException;
import vn.nghlong3004.boom.online.server.model.*;
import vn.nghlong3004.boom.online.server.model.request.GameActionRequest;
import vn.nghlong3004.boom.online.server.repository.RoomRepository;
import vn.nghlong3004.boom.online.server.service.GameService;
import vn.nghlong3004.boom.online.server.util.ItemSpawner;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

  private final ObjectMapper objectMapper;

  private final Map<String, ScheduledFuture<?>> scheduledTasks;
  private final SimpMessagingTemplate messagingTemplate;
  private final ItemSpawner itemSpawner;
  private final Map<String, GameState> games;
  private final TaskScheduler taskScheduler;
  private final RoomRepository roomRepository;

  @Override
  public void startGame(Room room) {
    GameState gameState = new GameState(room.getId());
    room.getSlots()
        .forEach(e -> gameState.addPlayer(String.valueOf(e.getUsername()), e.getDisplayName()));
    games.put(room.getId(), gameState);
    ScheduledFuture<?> scheduledTask =
        taskScheduler.schedule(
            () -> handleTimeout(room.getId()), Instant.now().plus(3, ChronoUnit.MINUTES));
    scheduledTasks.put(room.getId(), scheduledTask);
  }

  @Override
  public void handleAction(String roomId, GameActionRequest request) {
    switch (request.type()) {
      case MOVE -> handleMove(roomId, request);
      case PLACE_BOMB -> handlePlaceBomb(roomId, request);
      case PLAYER_HIT -> handlePlayerHit(roomId, request);
      case GAME_END -> handleGameEnd(roomId, request);
      case BRICK_DESTROYED -> handleBrickDestroyed(roomId, request);
      case ITEM_COLLECTED -> handleItemCollected(roomId, request);
      default -> log.debug("Unhandled action type: {}", request.type());
    }
  }

  private void handleItemCollected(String roomId, GameActionRequest request) {
    ItemCollectedData data = objectMapper.convertValue(request.data(), ItemCollectedData.class);
    log.info("Check collected with player Id: {}", request.playerId());
    broadcast(roomId, GameActionType.ITEM_COLLECTED, data, request.playerId());
  }

  private void handleBrickDestroyed(String roomId, GameActionRequest request) {
    BrickDestroyedData data = objectMapper.convertValue(request.data(), BrickDestroyedData.class);
    ItemSpawnedData itemSpawnedData =
        itemSpawner.trySpawnItem(data.tileX(), data.tileY(), data.tileType());
    if (itemSpawnedData != null) {
      broadcast(roomId, GameActionType.ITEM_SPAWNED, itemSpawnedData, null);
    }
  }

  private void handleGameEnd(String roomId, GameActionRequest request) {
    log.info("Start handle game end");
    GameEndData data = objectMapper.convertValue(request.data(), GameEndData.class);

    GameUpdate update =
        new GameUpdate(
            GameActionType.GAME_END, data, request.playerId(), System.currentTimeMillis());
    messagingTemplate.convertAndSend("/topic/game/" + roomId, update);

    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new ResourceException(ErrorCode.ROOM_NOT_FOUND));
    roomRepository.delete(room);

    log.info(
        "Game ended in room {} - winner: {}, reason: {}", roomId, data.winnerId(), data.reason());
  }

  private void handleMove(String roomId, GameActionRequest request) {
    broadcast(roomId, GameActionType.MOVE, request.data(), request.playerId());

    GameState gameState = games.get(roomId);
    if (gameState != null) {
      PlayerGameState player = gameState.getPlayers().get(request.playerId());
      if (player != null && request.data() instanceof Map) {
        MoveData moveData = objectMapper.convertValue(request.data(), MoveData.class);
        player.setX(moveData.x());
        player.setY(moveData.y());
        player.setDirection(moveData.direction());
      }
    }
  }

  private void handlePlaceBomb(String roomId, GameActionRequest request) {
    broadcast(roomId, GameActionType.PLACE_BOMB, request.data(), request.playerId());
  }

  private void handlePlayerHit(String roomId, GameActionRequest request) {
    GameState gameState = games.get(roomId);
    if (gameState == null) return;

    PlayerHitData playerHitData = objectMapper.convertValue(request.data(), PlayerHitData.class);

    PlayerGameState player = gameState.getPlayers().get(playerHitData.playerId());
    if (player != null && player.isAlive()) {
      player.hit();

      if (!player.isAlive()) {
        broadcast(roomId, GameActionType.PLAYER_DIED, null, playerHitData.playerId());
        checkWinCondition(roomId);
      } else {
        broadcast(
            roomId,
            GameActionType.PLAYER_HIT,
            Map.of("livesRemaining", player.getLives()),
            playerHitData.playerId());
      }
    }
  }

  private void checkWinCondition(String roomId) {
    GameState gameState = games.get(roomId);
    if (gameState == null || !gameState.isRunning()) return;

    long aliveCount = gameState.getAlivePlayers();
    if (aliveCount <= 1) {
      String winnerId = aliveCount == 1 ? gameState.getLastAlivePlayerId() : null;
      endGame(roomId, winnerId, "last_standing");
    }
  }

  private void handleTimeout(String roomId) {
    GameState gameState = games.get(roomId);
    if (gameState == null || !gameState.isRunning()) return;

    long aliveCount = gameState.getAlivePlayers();
    String winnerId = null;

    if (aliveCount == 1) {
      winnerId = gameState.getLastAlivePlayerId();
    }

    endGame(roomId, winnerId, "timeout");
  }

  @Override
  public void handleDisconnect(String roomId, String playerId) {
    GameState gameState = games.get(roomId);
    if (gameState == null || !gameState.isRunning()) return;

    gameState.markPlayerDead(playerId);

    checkWinCondition(roomId);
  }

  @Override
  public void endGame(String roomId, String winnerId, String reason) {
    GameState gameState = games.get(roomId);
    if (gameState == null || !gameState.isRunning()) {
      return;
    }
    gameState.setRunning(false);
    games.remove(roomId);

    cancelTimeoutTask(roomId);

    updateRoomStatusToWaiting(roomId);

    broadcastGameEnd(roomId, winnerId, reason);

    log.info("Game ended for room: {}, winner: {}, reason: {}", roomId, winnerId, reason);
  }

  private void broadcastGameEnd(String roomId, String winnerId, String reason) {
    GameEndData gameEndData = new GameEndData(winnerId != null ? winnerId : "", reason);
    broadcast(roomId, GameActionType.GAME_END, gameEndData, null);
  }

  private void cancelTimeoutTask(String roomId) {
    ScheduledFuture<?> task = scheduledTasks.remove(roomId);
    if (task != null && !task.isDone()) {
      task.cancel(false);
    }
  }

  public void updateRoomStatusToWaiting(String roomId) {
    roomRepository
        .findById(roomId)
        .ifPresent(
            room -> {
              room.setStatus(RoomStatus.WAITING);
              roomRepository.save(room);
            });
  }

  private void broadcast(
      String roomId, GameActionType gameActionType, Object data, String playerId) {
    GameUpdate update = new GameUpdate(gameActionType, data, playerId, System.currentTimeMillis());
    messagingTemplate.convertAndSend("/topic/game/" + roomId, update);
  }
}
