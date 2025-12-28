package vn.nghlong3004.boom.online.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import vn.nghlong3004.boom.online.server.model.Room;
import vn.nghlong3004.boom.online.server.model.request.RoomActionRequest;
import vn.nghlong3004.boom.online.server.service.RoomService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {
  private final RoomService roomService;

  @MessageMapping("/room/{roomId}/action")
  public Room handleAction(@DestinationVariable String roomId, @Payload RoomActionRequest request) {
    return roomService.processAction(roomId, request);
  }
}
