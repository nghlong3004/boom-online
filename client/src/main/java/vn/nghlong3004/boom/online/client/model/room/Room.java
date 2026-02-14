package vn.nghlong3004.boom.online.client.model.room;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Getter
@Setter
@Builder
public class Room {
  private String id;
  private String name;

  private Long ownerId;
  private String ownerDisplayName;

  private int mapIndex;
  private RoomStatus status;

  private List<PlayerSlot> slots;
  private List<ChatMessage> chat;

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
