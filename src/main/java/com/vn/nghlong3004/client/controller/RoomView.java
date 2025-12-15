package com.vn.nghlong3004.client.controller;

import com.vn.nghlong3004.client.model.room.RoomState;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public interface RoomView {
  void render(RoomState roomState, boolean isHost);

  void showError(String message);

  void backToList();
}
