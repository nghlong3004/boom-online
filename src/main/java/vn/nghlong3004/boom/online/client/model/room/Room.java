package vn.nghlong3004.boom.online.client.model.room;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Getter
@Builder
public class Room {
  private final String id;
  private final String name;

  private final Long ownerId;
  private final String ownerDisplayName;

  private final int mapIndex;
  private final RoomStatus status;

  private final List<PlayerSlot> slots;
  private final List<ChatMessage> chat;

  @Builder.Default private Instant created = Instant.now();

  @Builder.Default private final int maxPlayers = 4;

  public int getCurrentPlayers() {
    if (slots == null) return 0;
    int count = 0;
    for (PlayerSlot slot : slots) {
      if (slot != null && slot.isOccupied()) {
        count++;
      }
    }
    return count;
  }
}
