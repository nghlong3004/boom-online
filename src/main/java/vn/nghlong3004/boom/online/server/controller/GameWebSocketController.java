package vn.nghlong3004.boom.online.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import vn.nghlong3004.boom.online.server.model.request.GameActionRequest;
import vn.nghlong3004.boom.online.server.service.GameService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

  private final GameService gameService;

  @MessageMapping("/game/{roomId}/action")
  public void handleGameAction(@DestinationVariable String roomId, GameActionRequest request) {
    gameService.handleAction(roomId, request);
  }
}
