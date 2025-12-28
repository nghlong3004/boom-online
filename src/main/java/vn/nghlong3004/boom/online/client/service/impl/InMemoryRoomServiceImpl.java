package vn.nghlong3004.boom.online.client.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.random.RandomGeneratorFactory;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.client.model.room.*;
import vn.nghlong3004.boom.online.client.model.room.ChatMessage;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.session.UserSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;

/**
 * Project: boom-online-client
 *
 * <p>Simple in-memory implementation for OFFLINE mode.
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
public class InMemoryRoomServiceImpl implements RoomService {

  private Room currentRoom;

  @Override
  public CompletableFuture<Room> createRoom(User owner, String roomName) {
    Room newRoom =
        Room.builder()
            .id(UUID.randomUUID().toString())
            .name(I18NUtil.getString("room.base_name").formatted(owner.getDisplayName()))
            .ownerId(owner.getId())
            .ownerDisplayName(owner.getDisplayName())
            .mapIndex(0)
            .status(RoomStatus.WAITING)
            .maxPlayers(4)
            .created(Instant.now())
            .slots(new ArrayList<>(4))
            .chat(new ArrayList<>())
            .build();
    for (int i = 0; i < newRoom.getMaxPlayers(); ++i) {
      PlayerSlot slot = createBot();
      if (i == 0) {
        slot =
            PlayerSlot.builder()
                .index(0)
                .occupied(true)
                .bot(false)
                .userId(owner.getId())
                .displayName(owner.getDisplayName())
                .host(true)
                .ready(true)
                .characterIndex(0)
                .build();
      }
      newRoom.getSlots().add(slot);
    }
    currentRoom = newRoom;
    return CompletableFuture.completedFuture(newRoom);
  }

  @Override
  public CompletableFuture<Room> toggleReady() {
    long myId = UserSession.getInstance().getCurrentUser().getId();

    currentRoom.getSlots().stream()
        .filter(s -> s.isOccupied() && Objects.equals(s.getUserId(), myId))
        .findFirst()
        .ifPresent(slot -> slot.setReady(!slot.isReady()));

    return CompletableFuture.completedFuture(currentRoom);
  }

  @Override
  public CompletableFuture<Room> startGame() {
    boolean allReady =
        currentRoom.getSlots().stream()
            .filter(s -> s.isOccupied() && !s.isHost())
            .allMatch(PlayerSlot::isReady);
    if (allReady) {
      currentRoom.setStatus(RoomStatus.PLAYING);
    }
    return CompletableFuture.completedFuture(currentRoom);
  }

  @Override
  public CompletableFuture<RoomPageResponse> rooms(int pageIndex, int pageSize) {
    return null;
  }

  @Override
  public CompletableFuture<Room> joinRoom(String roomId, User user) {
    return null;
  }

  @Override
  public CompletableFuture<Room> changeMap(int mapIndex) {
    if (!Objects.equals(
        currentRoom.getOwnerId(), UserSession.getInstance().getCurrentUser().getId())) {
      return CompletableFuture.completedFuture(currentRoom);
    }

    currentRoom.setMapIndex(mapIndex);
    currentRoom.getChat().add(createSystemChat(I18NUtil.getString("room.change_map")));

    return CompletableFuture.completedFuture(currentRoom);
  }

  @Override
  public CompletableFuture<Room> changeCharacter(int characterIndex) {
    long myId = UserSession.getInstance().getCurrentUser().getId();
    currentRoom.getSlots().stream()
        .filter(s -> s.isOccupied() && Objects.equals(s.getUserId(), myId))
        .findFirst()
        .ifPresent(s -> s.setCharacterIndex(characterIndex));

    return CompletableFuture.completedFuture(currentRoom);
  }

  @Override
  public CompletableFuture<Room> sendChat(String content) {
    User user = UserSession.getInstance().getCurrentUser();
    currentRoom
        .getChat()
        .add(
            ChatMessage.builder()
                .type(ChatMessageType.USER)
                .senderDisplayName(user.getDisplayName())
                .content(content)
                .created(Instant.now())
                .build());
    return CompletableFuture.completedFuture(currentRoom);
  }

  private ChatMessage createSystemChat(String content) {
    return ChatMessage.builder()
        .type(ChatMessageType.SYSTEM)
        .content(content)
        .created(Instant.now())
        .build();
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
        .characterIndex(RandomGeneratorFactory.getDefault().create().nextInt() % 4)
        .build();
  }
}
