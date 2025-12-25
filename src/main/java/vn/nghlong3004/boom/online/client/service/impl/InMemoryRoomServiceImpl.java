package vn.nghlong3004.boom.online.client.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.client.model.room.*;
import vn.nghlong3004.boom.online.client.model.room.ChatMessageType;
import vn.nghlong3004.boom.online.client.service.RoomService;

/**
 * Project: boom-online-client
 *
 * <p>Simple in-memory implementation for OFFLINE mode.
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
public class InMemoryRoomServiceImpl implements RoomService {

  private final Map<String, Room> rooms = new ConcurrentHashMap<>();

  @Override
  public CompletableFuture<RoomPageResponse> rooms(int pageIndex, int pageSize) {
    return null;
  }

  @Override
  public CompletableFuture<Room> createRoom(User owner, String roomName) {
    return null;
  }

  private PlayerSlot createBot() {
    return PlayerSlot.builder()
        .index(1)
        .occupied(true)
        .bot(true)
        .userId(-1L)
        .displayName("Bot")
        .host(false)
        .ready(true)
        .characterIndex(1)
        .build();
  }

  @Override
  public Room joinRoom(String roomId, User user) {
    // Offline: not used.
    return getRoom(roomId);
  }

  @Override
  public Room leaveRoom(String roomId, User user) {
    // Offline: close room.
    rooms.remove(roomId);
    return null;
  }

  @Override
  public Room toggleReady(String roomId, User user) {
    // Offline: host is always ready.
    return getRoom(roomId);
  }

  @Override
  public Room changeMap(String roomId, User requester, int mapIndex) {
    Room room = requireRoom(roomId);
    if (!Objects.equals(room.getOwnerId(), requester.getId())) {
      return room;
    }
    Room updated =
        Room.builder()
            .id(room.getId())
            .name(room.getName())
            .ownerId(room.getOwnerId())
            .ownerDisplayName(room.getOwnerDisplayName())
            .mapIndex(normalizeMapIndex(mapIndex))
            .status(room.getStatus())
            .slots(room.getSlots())
            .chat(room.getChat())
            .build();
    rooms.put(roomId, updated);
    updated
        .getChat()
        .add(
            ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(ChatMessageType.SYSTEM)
                .content("Chủ phòng đã đổi map")
                .created(Instant.now())
                .build());
    return updated;
  }

  @Override
  public Room changeCharacter(String roomId, User user, int characterIndex) {
    Room room = requireRoom(roomId);
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
                .characterIndex(normalizeCharacterIndex(characterIndex))
                .build());
      } else {
        updatedSlots.add(slot);
      }
    }

    Room updated =
        Room.builder()
            .id(room.getId())
            .name(room.getName())
            .ownerId(room.getOwnerId())
            .ownerDisplayName(room.getOwnerDisplayName())
            .mapIndex(room.getMapIndex())
            .status(room.getStatus())
            .slots(updatedSlots)
            .chat(room.getChat())
            .build();

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

  private Room requireRoom(String roomId) {
    Room room = getRoom(roomId);
    if (room == null) {
      throw new IllegalArgumentException("Room not found: " + roomId);
    }
    return room;
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
