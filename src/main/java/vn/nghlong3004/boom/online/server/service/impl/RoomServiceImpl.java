package vn.nghlong3004.boom.online.server.service.impl;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.nghlong3004.boom.online.server.exception.ErrorCode;
import vn.nghlong3004.boom.online.server.exception.ResourceException;
import vn.nghlong3004.boom.online.server.model.*;
import vn.nghlong3004.boom.online.server.model.request.CreateRoomRequest;
import vn.nghlong3004.boom.online.server.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.server.repository.RoomRepository;
import vn.nghlong3004.boom.online.server.service.RoomService;
import vn.nghlong3004.boom.online.server.service.UserService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/24/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
  private final RoomRepository roomRepository;

  private final UserService userService;

  @Override
  public RoomPageResponse rooms(int pageIndex, int pageSize) {
    Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("created").descending());

    Page<Room> roomPage = roomRepository.findAll(pageable);

    return RoomPageResponse.builder()
        .rooms(roomPage.getContent())
        .pageIndex(roomPage.getNumber())
        .pageSize(roomPage.getSize())
        .totalRooms((int) roomPage.getTotalElements())
        .build();
  }

  @Override
  @Transactional
  public Room createRoom(CreateRoomRequest request) {
    AuthenticatedUser authenticatedUser = userService.getCurrentUser();
    Long userId = authenticatedUser.getId();
    String displayName = authenticatedUser.getDisplayName();
    Room room =
        Room.builder()
            .id(UUID.randomUUID().toString())
            .name(request.name())
            .ownerId(userId)
            .ownerDisplayName(displayName)
            .mapIndex(request.mapIndex())
            .status(RoomStatus.WAITING)
            .maxPlayers(4)
            .created(Instant.now())
            .slots(new ArrayList<>(4))
            .chat(new ArrayList<>())
            .build();

    for (int i = 0; i < room.getMaxPlayers(); i++) {
      PlayerSlot slot = PlayerSlot.builder().index(i).room(room).build();

      if (i == 0) {
        slot.setOccupied(true);
        slot.setUserId(userId);
        slot.setDisplayName(displayName);
        slot.setHost(true);
        slot.setReady(false);
        slot.setCharacterIndex(0);
      } else {
        slot.setOccupied(false);
        slot.setBot(false);
        slot.setHost(false);
        slot.setReady(false);
      }

      room.getSlots().add(slot);
    }

    ChatMessage sysMsg =
        ChatMessage.builder()
            .id(UUID.randomUUID().toString())
            .type(ChatMessageType.SYSTEM)
            .content(room.getName() + " đã được tạo.")
            .created(Instant.now())
            .room(room)
            .build();
    room.getChat().add(sysMsg);

    return roomRepository.save(room);
  }

  @Override
  @Transactional
  public Room joinRoom(String roomId) {
    AuthenticatedUser authenticatedUser = userService.getCurrentUser();
    Long userId = authenticatedUser.getId();
    String displayName = authenticatedUser.getDisplayName();

    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new ResourceException(ErrorCode.ROOM_NOT_FOUND));

    if (room.getStatus() == RoomStatus.PLAYING) {
      throw new ResourceException(ErrorCode.ROOM_PLAYING);
    }

    boolean alreadyInRoom =
        room.getSlots().stream()
            .anyMatch(slot -> slot.isOccupied() && userId.equals(slot.getUserId()));

    if (alreadyInRoom) {
      return room;
    }

    PlayerSlot emptySlot =
        room.getSlots().stream()
            .filter(slot -> !slot.isOccupied())
            .findFirst()
            .orElseThrow(() -> new ResourceException(ErrorCode.ROOM_FULL));

    emptySlot.setOccupied(true);
    emptySlot.setBot(false);
    emptySlot.setUserId(userId);
    emptySlot.setDisplayName(displayName);
    emptySlot.setHost(false);
    emptySlot.setReady(false);
    emptySlot.setCharacterIndex(0);

    ChatMessage joinMsg =
        ChatMessage.builder()
            .id(UUID.randomUUID().toString())
            .type(ChatMessageType.SYSTEM)
            .content(displayName + " đã vào phòng.")
            .created(Instant.now())
            .room(room)
            .build();
    room.getChat().add(joinMsg);

    return roomRepository.save(room);
  }
}
