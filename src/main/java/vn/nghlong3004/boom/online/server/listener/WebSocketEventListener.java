package vn.nghlong3004.boom.online.server.listener;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import vn.nghlong3004.boom.online.server.service.GameService;
import vn.nghlong3004.boom.online.server.service.RoomService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final RoomService roomService;
  private final GameService gameService;

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    Principal principal = event.getUser();
    if (principal != null) {
      String username = principal.getName();
      log.info("WebSocket Disconnected: {}", username);
      roomService.handleUserDisconnection(username);
      StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

      String sessionId = headerAccessor.getSessionId();
      String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
      String playerId = (String) headerAccessor.getSessionAttributes().get("playerId");

      if (roomId != null && playerId != null) {
        log.info("Player {} disconnected from room {}", playerId, roomId);
        gameService.handleDisconnect(roomId, playerId);
      }
    } else {
      log.info("Principal is null");
    }
  }
}
