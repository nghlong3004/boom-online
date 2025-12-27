package vn.nghlong3004.boom.online.server.service.impl;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import vn.nghlong3004.boom.online.server.exception.ErrorCode;
import vn.nghlong3004.boom.online.server.exception.ResourceException;
import vn.nghlong3004.boom.online.server.model.*;
import vn.nghlong3004.boom.online.server.model.request.CreateRoomRequest;
import vn.nghlong3004.boom.online.server.model.request.RoomActionRequest;
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
  private final SimpMessagingTemplate messagingTemplate;

  private final UserService userService;
  private final UserDetailsService userDetailsService;

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
        slot.setReady(true);
        slot.setCharacterIndex(0);
      } else {
        slot.setOccupied(false);
        slot.setBot(false);
        slot.setHost(false);
        slot.setReady(false);
      }

      room.getSlots().add(slot);
    }

    addSystemMessage(room, room.getName() + " đã được tạo.");

    return roomRepository.save(room);
  }

  @Override
  @Transactional
  public Room joinRoom(String roomId) {
    AuthenticatedUser user = userService.getCurrentUser();
    Long userId = user.getId();
    String displayName = user.getDisplayName();
    Room room = getRoom(roomId);

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

    updateSlot(emptySlot, userId, displayName);
    addSystemMessage(room, displayName + " joined the room.");

    Room savedRoom = roomRepository.save(room);

    broadcastRoomUpdate(savedRoom);

    return savedRoom;
  }

  @Override
  @Transactional
  public void leaveRoom(String roomId, AuthenticatedUser user) {
    Room room = getRoom(roomId);
    Long userId = user.getId();

    PlayerSlot slot = findSlotByUser(room, userId);
    if (slot != null) {
      slot.setOccupied(false);
      slot.setUserId(null);
      slot.setDisplayName(null);
      slot.setReady(false);
      slot.setHost(false);

      addSystemMessage(room, user.getDisplayName() + " left the room.");

      if (userId.equals(room.getOwnerId())) {
        handleOwnerLeave(room);
      }

      broadcastRoomUpdate(room);
    }
  }

  @Override
  @Transactional
  public Room processAction(String roomId, RoomActionRequest request) {
    AuthenticatedUser user =
        (AuthenticatedUser) userDetailsService.loadUserByUsername(request.username());
    Room room = getRoom(roomId);

    switch (request.type()) {
      case CHAT -> {
        if (request.data() == null) throw new IllegalArgumentException("Nội dung chat trống");
        sendChat(room, user, request.data().toString());
      }
      case CHANGE_MAP -> {
        int mapIndex = parseSafeInteger(request.data());
        changeMap(room, user, mapIndex);
      }
      case CHANGE_CHARACTER -> {
        int charIndex = parseSafeInteger(request.data());
        changeCharacter(room, user, charIndex);
      }
      case READY -> toggleReady(room, user);

      case START -> startGame(room, user);

      default -> log.warn("Unknown action type: {}", request.type());
    }

    Room savedRoom = roomRepository.save(room);
    broadcastRoomUpdate(savedRoom);
    return savedRoom;
  }

  @Override
  @Transactional
  public void handleUserDisconnection(String username) {
    AuthenticatedUser userEntity =
        (AuthenticatedUser) userDetailsService.loadUserByUsername(username);

    var roomOptional = roomRepository.findRoomByUserId(userEntity.getId());

    if (roomOptional.isPresent()) {
      Room room = roomOptional.get();
      log.info("User {} disconnected abnormally. Cleaning up room {}", username, room.getId());

      leaveRoom(room.getId(), userEntity);
    }
  }

  private Room getRoom(String roomId) {
    return roomRepository
        .findById(roomId)
        .orElseThrow(() -> new ResourceException(ErrorCode.ROOM_NOT_FOUND));
  }

  private void sendChat(Room room, AuthenticatedUser user, String content) {
    ChatMessage message =
        ChatMessage.builder()
            .id(UUID.randomUUID().toString())
            .type(ChatMessageType.USER)
            .senderId(user.getId())
            .senderDisplayName(user.getDisplayName())
            .content(content)
            .created(Instant.now())
            .room(room)
            .build();
    room.getChat().add(message);

    if (room.getChat().size() > 50) {
      room.getChat().removeFirst();
    }
  }

  private void changeMap(Room room, AuthenticatedUser user, int mapIndex) {
    if (!Objects.equals(room.getOwnerId(), user.getId())) {
      log.warn("User {} tried to change map but is not owner", user.getDisplayName());
      return;
    }
    room.setMapIndex(mapIndex);
    unreadyAllExceptHost(room);
  }

  private void changeCharacter(Room room, AuthenticatedUser user, int charIndex) {
    PlayerSlot slot = findSlotByUser(room, user.getId());
    if (slot != null) {
      slot.setCharacterIndex(charIndex);
    }
  }

  private void toggleReady(Room room, AuthenticatedUser user) {
    PlayerSlot slot = findSlotByUser(room, user.getId());
    if (slot != null) {
      if (slot.isHost()) return;
      slot.setReady(!slot.isReady());
    }
  }

  private void startGame(Room room, AuthenticatedUser user) {
    if (!Objects.equals(room.getOwnerId(), user.getId())) return;
    boolean allReady =
        room.getSlots().stream().filter(PlayerSlot::isOccupied).allMatch(PlayerSlot::isReady);

    if (allReady) {
      room.setStatus(RoomStatus.PLAYING);
      addSystemMessage(room, "Trận đấu bắt đầu!");
    } else {
      addSystemMessage(room, "Không thể bắt đầu, chưa đủ người sẵn sàng.");
    }
  }

  private PlayerSlot findSlotByUser(Room room, Long userId) {
    return room.getSlots().stream()
        .filter(s -> s.isOccupied() && Objects.equals(s.getUserId(), userId))
        .findFirst()
        .orElse(null);
  }

  private void unreadyAllExceptHost(Room room) {
    room.getSlots()
        .forEach(
            slot -> {
              if (slot.isOccupied() && !slot.isHost()) {
                slot.setReady(false);
              }
            });
  }

  private void handleOwnerLeave(Room room) {
    room.getSlots().stream()
        .filter(PlayerSlot::isOccupied)
        .findFirst()
        .ifPresentOrElse(
            newOwner -> {
              newOwner.setHost(true);
              newOwner.setReady(true);
              room.setOwnerId(newOwner.getUserId());
              room.setOwnerDisplayName(newOwner.getDisplayName());
              addSystemMessage(room, newOwner.getDisplayName() + " bây giờ là chủ phòng.");
            },
            () -> roomRepository.delete(room));
  }

  private int parseSafeInteger(Object data) {
    if (data instanceof Integer) return (Integer) data;
    if (data instanceof Double) return ((Double) data).intValue();
    if (data instanceof String) {
      try {
        return Integer.parseInt((String) data);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    return 0;
  }

  private void updateSlot(PlayerSlot slot, Long userId, String displayName) {
    slot.setOccupied(true);
    slot.setBot(false);
    slot.setUserId(userId);
    slot.setDisplayName(displayName);
    slot.setHost(false);
    slot.setReady(false);
    slot.setCharacterIndex(0);
  }

  private void addSystemMessage(Room room, String content) {
    ChatMessage msg =
        ChatMessage.builder()
            .id(UUID.randomUUID().toString())
            .type(ChatMessageType.SYSTEM)
            .content(content)
            .created(Instant.now())
            .room(room)
            .build();
    room.getChat().add(msg);
  }

  private void broadcastRoomUpdate(Room room) {
    String destination = "/topic/room/" + room.getId();
    messagingTemplate.convertAndSend(destination, room);
  }
}
