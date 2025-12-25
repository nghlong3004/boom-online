package vn.nghlong3004.boom.online.client.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.request.CreateRoomRequest;
import vn.nghlong3004.boom.online.client.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.client.model.room.*;
import vn.nghlong3004.boom.online.client.model.room.ChatMessageType;
import vn.nghlong3004.boom.online.client.model.room.RoomStatus;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

  private final Map<String, Room> rooms;
  private final HttpService httpService;

  @Override
  public CompletableFuture<RoomPageResponse> rooms(int pageIndex, int pageSize) {
    String token = UserSession.getInstance().getAccessToken();
    return httpService.getRooms(pageIndex, pageSize, token);
  }

  @Override
  public CompletableFuture<Room> createRoom(User owner, String roomName) {
    String token = UserSession.getInstance().getAccessToken();

    CreateRoomRequest request = new CreateRoomRequest(roomName, 0);

    return httpService.createRoom(request, token);
  }

  @Override
  public Room joinRoom(String roomId, User user) {
    Objects.requireNonNull(user, "user");
    Room room = requireRoom(roomId);

    for (PlayerSlot slot : room.getSlots()) {
      if (slot != null && slot.isOccupied() && Objects.equals(slot.getUserId(), user.getId())) {
        return room;
      }
    }

    int emptyIndex = -1;
    for (int i = 0; i < room.getSlots().size(); i++) {
      PlayerSlot slot = room.getSlots().get(i);
      if (slot == null || !slot.isOccupied()) {
        emptyIndex = i;
        break;
      }
    }
    if (emptyIndex < 0) {
      return room;
    }

    List<PlayerSlot> updatedSlots = new ArrayList<>(room.getSlots());
    updatedSlots.set(
        emptyIndex,
        PlayerSlot.builder()
            .index(emptyIndex)
            .occupied(true)
            .bot(false)
            .userId(user.getId())
            .displayName(user.getDisplayName())
            .host(false)
            .ready(false)
            .characterIndex(0)
            .build());

    Room updated = copyWith(room, updatedSlots, room.getMapIndex(), room.getStatus());
    updated
        .getChat()
        .add(
            ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(ChatMessageType.SYSTEM)
                .content(user.getDisplayName() + " đã vào phòng")
                .created(Instant.now())
                .build());

    rooms.put(roomId, updated);
    return updated;
  }

  @Override
  public Room leaveRoom(String roomId, User user) {
    Objects.requireNonNull(user, "user");
    Room room = requireRoom(roomId);

    List<PlayerSlot> updatedSlots = new ArrayList<>(room.getSlots());

    boolean removed = false;
    boolean wasOwner = Objects.equals(room.getOwnerId(), user.getId());
    for (int i = 0; i < updatedSlots.size(); i++) {
      PlayerSlot slot = updatedSlots.get(i);
      if (slot != null && slot.isOccupied() && Objects.equals(slot.getUserId(), user.getId())) {
        updatedSlots.set(i, PlayerSlot.builder().index(i).occupied(false).bot(false).build());
        removed = true;
        break;
      }
    }

    Room updated = copyWith(room, updatedSlots, room.getMapIndex(), room.getStatus());
    if (removed) {
      updated
          .getChat()
          .add(
              ChatMessage.builder()
                  .id(UUID.randomUUID().toString())
                  .type(ChatMessageType.SYSTEM)
                  .content(user.getDisplayName() + " đã rời phòng")
                  .created(Instant.now())
                  .build());
    }

    // If owner left, pick first occupied player as new owner.
    if (wasOwner) {
      Long newOwnerId = null;
      String newOwnerName = null;
      for (PlayerSlot slot : updated.getSlots()) {
        if (slot != null && slot.isOccupied() && !slot.isBot()) {
          newOwnerId = slot.getUserId();
          newOwnerName = slot.getDisplayName();
          break;
        }
      }
      if (newOwnerId == null) {
        rooms.remove(roomId);
        return null;
      }

      updated =
          Room.builder()
              .id(updated.getId())
              .name(updated.getName())
              .ownerId(newOwnerId)
              .ownerDisplayName(newOwnerName)
              .mapIndex(updated.getMapIndex())
              .status(updated.getStatus())
              .slots(updated.getSlots())
              .chat(updated.getChat())
              .build();
      updated
          .getChat()
          .add(
              ChatMessage.builder()
                  .id(UUID.randomUUID().toString())
                  .type(ChatMessageType.SYSTEM)
                  .content(newOwnerName + " đã trở thành chủ phòng")
                  .created(Instant.now())
                  .build());
    }

    rooms.put(roomId, updated);
    return updated;
  }

  @Override
  public Room toggleReady(String roomId, User user) {
    Objects.requireNonNull(user, "user");
    Room room = requireRoom(roomId);

    List<PlayerSlot> updatedSlots = new ArrayList<>(room.getSlots().size());
    boolean changed = false;

    for (PlayerSlot slot : room.getSlots()) {
      if (slot != null && slot.isOccupied() && Objects.equals(slot.getUserId(), user.getId())) {
        if (slot.isHost()) {
          updatedSlots.add(slot);
        } else {
          updatedSlots.add(
              PlayerSlot.builder()
                  .index(slot.getIndex())
                  .occupied(true)
                  .bot(false)
                  .userId(slot.getUserId())
                  .displayName(slot.getDisplayName())
                  .host(false)
                  .ready(!slot.isReady())
                  .characterIndex(slot.getCharacterIndex())
                  .build());
          changed = true;
        }
      } else {
        updatedSlots.add(slot);
      }
    }

    Room updated = copyWith(room, updatedSlots, room.getMapIndex(), room.getStatus());
    if (changed) {
      updated
          .getChat()
          .add(
              ChatMessage.builder()
                  .id(UUID.randomUUID().toString())
                  .type(ChatMessageType.SYSTEM)
                  .content(user.getDisplayName() + " đã thay đổi trạng thái sẵn sàng")
                  .created(Instant.now())
                  .build());
    }

    rooms.put(roomId, updated);
    return updated;
  }

  @Override
  public Room changeMap(String roomId, User requester, int mapIndex) {
    Objects.requireNonNull(requester, "requester");
    Room room = requireRoom(roomId);
    if (!Objects.equals(room.getOwnerId(), requester.getId())) {
      return room;
    }

    int normalized = normalizeMapIndex(mapIndex);
    Room updated = copyWith(room, room.getSlots(), normalized, room.getStatus());
    updated
        .getChat()
        .add(
            ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(ChatMessageType.SYSTEM)
                .content("Chủ phòng đã đổi map")
                .created(Instant.now())
                .build());

    rooms.put(roomId, updated);
    return updated;
  }

  @Override
  public Room changeCharacter(String roomId, User user, int characterIndex) {
    Objects.requireNonNull(user, "user");
    Room room = requireRoom(roomId);

    int normalized = normalizeCharacterIndex(characterIndex);
    List<PlayerSlot> updatedSlots = new ArrayList<>(room.getSlots().size());

    for (PlayerSlot slot : room.getSlots()) {
      if (slot != null && slot.isOccupied() && Objects.equals(slot.getUserId(), user.getId())) {
        updatedSlots.add(
            PlayerSlot.builder()
                .index(slot.getIndex())
                .occupied(true)
                .bot(slot.isBot())
                .userId(slot.getUserId())
                .displayName(slot.getDisplayName())
                .host(slot.isHost())
                .ready(slot.isReady())
                .characterIndex(normalized)
                .build());
      } else {
        updatedSlots.add(slot);
      }
    }

    Room updated = copyWith(room, updatedSlots, room.getMapIndex(), room.getStatus());
    rooms.put(roomId, updated);
    return updated;
  }

  @Override
  public void sendChat(String roomId, User user, String content) {
    Room room = requireRoom(roomId);

    ChatMessage msg =
        ChatMessage.builder()
            .id(UUID.randomUUID().toString())
            .type(ChatMessageType.USER)
            .senderId(user != null ? user.getId() : null)
            .senderDisplayName(user != null ? user.getDisplayName() : null)
            .content(content.trim())
            .created(Instant.now())
            .build();

    room.getChat().add(msg);
  }

  @Override
  public Room getRoom(String roomId) {
    if (roomId == null) return null;
    return rooms.get(roomId);
  }

  private void seedRooms() {
    // Create a few demo rooms so Lobby isn't empty on first run.
    for (int i = 1; i <= 5; i++) {
      String id = "DEMO-" + i;
      List<PlayerSlot> slots = new ArrayList<>(4);
      slots.add(
          PlayerSlot.builder()
              .index(0)
              .occupied(true)
              .bot(false)
              .userId(100L + i)
              .displayName("Host " + i)
              .host(true)
              .ready(false)
              .characterIndex(i % RoomConstant.PLAYER_AVATARS.length)
              .build());
      for (int s = 1; s < 4; s++) {
        slots.add(PlayerSlot.builder().index(s).occupied(false).bot(false).build());
      }
      List<ChatMessage> chat = new CopyOnWriteArrayList<>();
      Room room =
          Room.builder()
              .id(id)
              .name("Demo Room " + i)
              .ownerId(100L + i)
              .ownerDisplayName("Host " + i)
              .mapIndex(i % RoomConstant.MAP_AVATARS.length)
              .status(RoomStatus.WAITING)
              .slots(slots)
              .chat(chat)
              .build();
      rooms.put(id, room);
    }
  }

  private Room requireRoom(String roomId) {
    Room room = getRoom(roomId);
    if (room == null) {
      throw new IllegalArgumentException("Room not found: " + roomId);
    }
    return room;
  }

  private Room copyWith(Room base, List<PlayerSlot> slots, int mapIndex, RoomStatus status) {
    return Room.builder()
        .id(base.getId())
        .name(base.getName())
        .ownerId(base.getOwnerId())
        .ownerDisplayName(base.getOwnerDisplayName())
        .mapIndex(mapIndex)
        .status(status)
        .slots(slots)
        .chat(base.getChat())
        .build();
  }

  private RoomPageResponse page(List<Room> rooms, int pageIndex, int pageSize) {
    int total = rooms.size();
    int start = Math.max(0, pageIndex * pageSize);
    int end = Math.min(total, start + pageSize);
    List<Room> sub = (start >= end) ? List.of() : rooms.subList(start, end);
    return RoomPageResponse.builder()
        .rooms(new ArrayList<>(sub))
        .pageIndex(pageIndex)
        .pageSize(pageSize)
        .totalRooms(total)
        .build();
  }

  private int normalizeMapIndex(int idx) {
    int n = RoomConstant.MAP_AVATARS.length;
    if (n == 0) return 0;
    return Math.floorMod(idx, n);
  }

  private int normalizeCharacterIndex(int idx) {
    int n = RoomConstant.PLAYER_AVATARS.length;
    if (n == 0) return 0;
    return Math.floorMod(idx, n);
  }
}
