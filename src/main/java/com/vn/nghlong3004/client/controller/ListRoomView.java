package com.vn.nghlong3004.client.controller;

import com.vn.nghlong3004.client.model.room.RoomState;
import com.vn.nghlong3004.client.model.room.RoomSummary;
import java.util.List;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public interface ListRoomView {
  void showRooms(List<RoomSummary> rooms, int pageIndex, int totalPages);

  void openRoom(RoomState roomState);

  void showError(String message);
}
