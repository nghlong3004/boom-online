package vn.nghlong3004.boom.online.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nghlong3004.boom.online.server.model.Room;
import vn.nghlong3004.boom.online.server.model.request.CreateRoomRequest;
import vn.nghlong3004.boom.online.server.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.server.service.RoomService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/24/2025
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public RoomPageResponse rooms(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
    return roomService.rooms(page, size);
  }

  @PostMapping("/create")
  @ResponseStatus(HttpStatus.OK)
  public Room createRoom(@Valid @RequestBody CreateRoomRequest request) {
    return roomService.createRoom(request);
  }

  @PostMapping("/{roomId}/join")
  @ResponseStatus(HttpStatus.OK)
  public Room joinRoom(@PathVariable String roomId) {
    return roomService.joinRoom(roomId);
  }
}
