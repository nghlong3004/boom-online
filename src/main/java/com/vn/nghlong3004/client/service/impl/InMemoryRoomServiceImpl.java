package com.vn.nghlong3004.client.service.impl;

import com.vn.nghlong3004.client.model.room.ChatMessage;
import com.vn.nghlong3004.client.model.room.PlayerSlot;
import com.vn.nghlong3004.client.model.room.RoomMode;
import com.vn.nghlong3004.client.model.room.RoomState;
import com.vn.nghlong3004.client.model.room.RoomSummary;
import com.vn.nghlong3004.client.service.RoomService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public class InMemoryRoomServiceImpl implements RoomService {

  private final Map<String, RoomState> rooms = new ConcurrentHashMap<>();

  public static InMemoryRoomServiceImpl getInstance() {
    return Holder.INSTANCE;
  }

  private InMemoryRoomServiceImpl() {
    for (int i = 1; i <= 8; i++) {
      RoomState room =
          new RoomState(
              UUID.randomUUID().toString(),
              String.format(text("room.seed.name_format"), i),
              RoomMode.ONLINE);
      rooms.put(room.getId(), room);
      room.getChatHistory()
          .add(ChatMessage.system(String.format(text("room.chat.welcome_format"), room.getName())));
    }
  }

  @Override
  public List<RoomSummary> listRooms() {
    List<RoomSummary> list = new ArrayList<>();
    for (RoomState room : rooms.values()) {
      if (room.getMode() == RoomMode.ONLINE) {
        list.add(
            new RoomSummary(
                room.getId(),
                room.getName(),
                room.getMode(),
                room.getCurrentPlayers(),
                room.getMaxPlayers()));
      }
    }
    list.sort(Comparator.comparing(RoomSummary::name));
    return list;
  }

  @Override
  public synchronized RoomState createRoom(String hostName, RoomMode mode) {
    String id = UUID.randomUUID().toString();
    String name =
        (mode == RoomMode.OFFLINE)
            ? text("room.default_name.offline")
            : text("room.default_name.online");
    RoomState room = new RoomState(id, name, mode);

    PlayerSlot hostSlot = room.getSlots().getFirst();
    hostSlot.setPlayerName(hostName);
    hostSlot.setHost(true);
    hostSlot.setReady(true);
    hostSlot.setCharacterIndex(0);

    if (mode == RoomMode.OFFLINE) {
      for (int i = 1; i < room.getSlots().size(); i++) {
        PlayerSlot cpu = room.getSlots().get(i);
        cpu.setPlayerName("CPU " + i);
        cpu.setMachine(true);
        cpu.setHost(false);
        cpu.setReady(true);
        cpu.setCharacterIndex(i % 4);
      }
    }

    room.getChatHistory()
        .add(ChatMessage.system(String.format(text("room.chat.created_format"), hostName)));
    rooms.put(id, room);
    return room;
  }

  @Override
  public synchronized RoomState joinRoom(String roomId, String playerName) {
    RoomState room = getExistingRoom(roomId);
    if (room.getMode() != RoomMode.ONLINE) {
      throw new IllegalStateException("Cannot join offline room");
    }
    for (PlayerSlot slot : room.getSlots()) {
      if (!slot.isEmpty() && playerName.equalsIgnoreCase(slot.getPlayerName())) {
        return room;
      }
    }

    PlayerSlot empty =
        room.getSlots().stream().filter(PlayerSlot::isEmpty).findFirst().orElse(null);
    if (empty == null) {
      throw new IllegalStateException("Room is full");
    }
    empty.setPlayerName(playerName);
    empty.setHost(false);
    empty.setReady(false);
    empty.setMachine(false);
    empty.setCharacterIndex(0);

    room.getChatHistory()
        .add(ChatMessage.system(String.format(text("room.chat.joined_format"), playerName)));
    return room;
  }

  @Override
  public RoomState getRoom(String roomId) {
    return getExistingRoom(roomId);
  }

  @Override
  public synchronized RoomState leaveRoom(String roomId, String playerName) {
    RoomState room = getExistingRoom(roomId);

    PlayerSlot leaving =
        room.getSlots().stream()
            .filter(
                s ->
                    !s.isEmpty()
                        && !s.isMachine()
                        && playerName.equalsIgnoreCase(s.getPlayerName()))
            .findFirst()
            .orElse(null);

    if (leaving == null) {
      return room;
    }

    boolean wasHost = leaving.isHost();
    leaving.setPlayerName(null);
    leaving.setHost(false);
    leaving.setReady(false);
    leaving.setMachine(false);
    leaving.setCharacterIndex(0);

    room.getChatHistory()
        .add(ChatMessage.system(String.format(text("room.chat.left_format"), playerName)));

    if (room.getMode() == RoomMode.ONLINE && wasHost) {
      PlayerSlot nextHost =
          room.getSlots().stream()
              .filter(s -> !s.isEmpty() && !s.isMachine())
              .findFirst()
              .orElse(null);
      if (nextHost != null) {
        nextHost.setHost(true);
        nextHost.setReady(true);
        room.getChatHistory()
            .add(
                ChatMessage.system(
                    String.format(text("room.chat.new_host_format"), nextHost.getPlayerName())));
      }
    }

    if (room.getMode() == RoomMode.ONLINE && room.getCurrentPlayers() == 0) {
      rooms.remove(roomId);
      return room;
    }

    return room;
  }

  @Override
  public synchronized RoomState toggleReady(String roomId, String playerName) {
    RoomState room = getExistingRoom(roomId);
    PlayerSlot slot = findPlayerSlot(room, playerName);
    if (slot == null) {
      return room;
    }
    if (slot.isHost()) {
      slot.setReady(true);
      return room;
    }
    slot.setReady(!slot.isReady());
    room.getChatHistory()
        .add(
            ChatMessage.system(
                slot.isReady()
                    ? String.format(text("room.chat.ready_format"), playerName)
                    : String.format(text("room.chat.not_ready_format"), playerName)));
    return room;
  }

  @Override
  public synchronized RoomState selectCharacter(
      String roomId, String playerName, int characterIndex) {
    RoomState room = getExistingRoom(roomId);
    PlayerSlot slot = findPlayerSlot(room, playerName);
    if (slot != null) {
      slot.setCharacterIndex(Math.floorMod(characterIndex, 4));
    }
    return room;
  }

  @Override
  public synchronized RoomState selectMap(String roomId, int mapIndex) {
    RoomState room = getExistingRoom(roomId);
    room.setMapIndex(Math.floorMod(mapIndex, 4));
    room.getChatHistory().add(ChatMessage.system(text("room.chat.map_changed")));
    return room;
  }

  @Override
  public synchronized RoomState addChat(String roomId, ChatMessage message) {
    RoomState room = getExistingRoom(roomId);
    if (message != null && message.content() != null && !message.content().isBlank()) {
      room.getChatHistory().add(message);
    }
    return room;
  }

  private RoomState getExistingRoom(String roomId) {
    RoomState room = rooms.get(roomId);
    if (room == null) {
      throw new IllegalArgumentException("Room not found");
    }
    return room;
  }

  private PlayerSlot findPlayerSlot(RoomState room, String playerName) {
    return room.getSlots().stream()
        .filter(
            s -> !s.isEmpty() && !s.isMachine() && playerName.equalsIgnoreCase(s.getPlayerName()))
        .findFirst()
        .orElse(null);
  }

  private String text(String key) {
    return LanguageUtil.getInstance().getString(key);
  }

  private static final class Holder {
    private static final InMemoryRoomServiceImpl INSTANCE = new InMemoryRoomServiceImpl();
  }
}
