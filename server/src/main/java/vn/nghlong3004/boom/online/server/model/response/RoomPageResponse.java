package vn.nghlong3004.boom.online.server.model.response;

import java.util.List;
import lombok.Builder;
import vn.nghlong3004.boom.online.server.model.Room;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/24/2025
 */
@Builder
public record RoomPageResponse(List<Room> rooms, int pageIndex, int pageSize, int totalRooms) {
  public int getTotalPages() {
    if (pageSize <= 0) return 0;
    return (int) Math.ceil(totalRooms / (double) pageSize);
  }
}
