package vn.nghlong3004.boom.online.server.service.impl;

import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import vn.nghlong3004.boom.online.server.exception.ErrorCode;
import vn.nghlong3004.boom.online.server.exception.ResourceException;
import vn.nghlong3004.boom.online.server.model.*;
import vn.nghlong3004.boom.online.server.model.request.GameActionRequest;
import vn.nghlong3004.boom.online.server.repository.RoomRepository;
import vn.nghlong3004.boom.online.server.service.GameService;

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

  private final SimpMessagingTemplate messagingTemplate;
  private final Map<String, GameState> games = new ConcurrentHashMap<>();
  private final Map<String, ScheduledFuture<?>> gameTimers = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
  private final RoomRepository roomRepository;

  @Override
  public void startGame(Room room) {
    GameState gameState = new GameState(room.getId());
    room.getSlots()
        .forEach(e -> gameState.addPlayer(String.valueOf(e.getUsername()), e.getDisplayName()));
    games.put(room.getId(), gameState);
  }

  @Override
  public void handleAction(String roomId, GameActionRequest request) {
    log.info("handler action type: {}", request.type());
    switch (request.type()) {
      case MOVE -> handleMove(roomId, request);
      case PLACE_BOMB -> handlePlaceBomb(roomId, request);
      case PLAYER_HIT -> handlePlayerHit(roomId, request);
      case GAME_END -> handleGameEnd(roomId, request);
      default -> log.debug("Unhandled action type: {}", request.type());
    }
  }

  private void handleGameEnd(String roomId, GameActionRequest request) {
    log.info("Start handle game end");
    GameEndData data = convertToGameEndData(request.data());

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

  private GameEndData convertToGameEndData(Object data) {
    return objectMapper.convertValue(data, GameEndData.class);
  }

  private void handleMove(String roomId, GameActionRequest request) {
    broadcast(
        roomId,
        new GameUpdate(
            GameActionType.MOVE, request.data(), request.playerId(), System.currentTimeMillis()));

    GameState gameState = games.get(roomId);
    if (gameState != null) {
      PlayerGameState player = gameState.getPlayers().get(request.playerId());
      if (player != null && request.data() instanceof Map) {
        Map<String, Object> data = (Map<String, Object>) request.data();
        player.setX(((Number) data.get("x")).floatValue());
        player.setY(((Number) data.get("y")).floatValue());
        player.setDirection((String) data.get("direction"));
      }
    }
  }

  private void handlePlaceBomb(String roomId, GameActionRequest request) {
    broadcast(
        roomId,
        new GameUpdate(
            GameActionType.PLACE_BOMB,
            request.data(),
            request.playerId(),
            System.currentTimeMillis()));
  }

  private void handlePlayerHit(String roomId, GameActionRequest request) {
    GameState gameState = games.get(roomId);
    if (gameState == null) return;

    Map<String, Object> data = (Map<String, Object>) request.data();
    String hitPlayerId = (String) data.get("playerId");

    PlayerGameState player = gameState.getPlayers().get(hitPlayerId);
    if (player != null && player.isAlive()) {
      player.hit();

      if (!player.isAlive()) {
        broadcast(
            roomId,
            new GameUpdate(
                GameActionType.PLAYER_DIED, null, hitPlayerId, System.currentTimeMillis()));

        checkWinCondition(roomId);
      } else {
        broadcast(
            roomId,
            new GameUpdate(
                GameActionType.PLAYER_HIT,
                Map.of("livesRemaining", player.getLives()),
                hitPlayerId,
                System.currentTimeMillis()));
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

    broadcast(
        roomId,
        new GameUpdate(
            GameActionType.PLAYER_DIED,
            Map.of("reason", "disconnect"),
            playerId,
            System.currentTimeMillis()));

    checkWinCondition(roomId);
  }

  @Override
  @Transactional
  public void endGame(String roomId, String winnerId, String reason) {
    GameState gameState = games.get(roomId);
    if (gameState == null) return;

    gameState.setRunning(false);

    ScheduledFuture<?> timer = gameTimers.remove(roomId);
    if (timer != null) {
      timer.cancel(false);
    }

    broadcast(
        roomId,
        new GameUpdate(
            GameActionType.GAME_END,
            Map.of("winnerId", winnerId != null ? winnerId : "", "reason", reason),
            null,
            System.currentTimeMillis()));
    Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceException(ErrorCode.ROOM_NOT_FOUND));
    room.setStatus(RoomStatus.WAITING);
    log.info("Game ended for room: {}, winner: {}, reason: {}", roomId, winnerId, reason);
    scheduler.schedule(() -> cleanup(roomId), 5, TimeUnit.SECONDS);
  }

  private void broadcast(String roomId, GameUpdate update) {
    messagingTemplate.convertAndSend("/topic/game/" + roomId, update);
  }

  private void cleanup(String roomId) {
    games.remove(roomId);
    log.info("Cleaned up game state for room: {}", roomId);
  }

  public GameState getGameState(String roomId) {
    return games.get(roomId);
  }
}
