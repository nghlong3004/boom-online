package vn.nghlong3004.boom.online.client.model.response;

import java.util.List;
import lombok.*;
import vn.nghlong3004.boom.online.client.model.room.Room;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPageResponse {
  private List<Room> rooms;
  private int pageIndex;
  private int pageSize;
  private int totalRooms;

  public int getTotalPages() {
    if (pageSize <= 0) return 0;
    return (int) Math.ceil(totalRooms / (double) pageSize);
  }
}
