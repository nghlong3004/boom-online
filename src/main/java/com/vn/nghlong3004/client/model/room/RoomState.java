package com.vn.nghlong3004.client.model.room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
@Getter
@Setter
public class RoomState {
  private String id;
  private String name;
  private RoomMode mode;

  private int mapIndex;
  private final List<PlayerSlot> slots = new ArrayList<>(4);
  private final List<ChatMessage> chatHistory = new ArrayList<>();

  public RoomState(String id, String name, RoomMode mode) {
    this.id = id;
    this.name = name;
    this.mode = mode;
    for (int i = 0; i < 4; i++) {
      slots.add(new PlayerSlot(i, null, false, false, 0, false));
    }
  }

  public List<PlayerSlot> viewSlots() {
    return Collections.unmodifiableList(slots);
  }

  public List<ChatMessage> viewChatHistory() {
    return Collections.unmodifiableList(chatHistory);
  }

  public int getCurrentPlayers() {
    int count = 0;
    for (PlayerSlot slot : slots) {
      if (!slot.isEmpty() && !slot.isMachine()) {
        count++;
      }
    }
    return count;
  }

  public int getMaxPlayers() {
    return 4;
  }
}
