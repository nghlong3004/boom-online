package com.vn.nghlong3004.client.controller;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public interface ListRoomPresenter {
  void loadRooms();

  void nextPage();

  void previousPage();

  void onCreateRoomClicked();

  void onBackClicked();

  void onJoinRoomDoubleClicked(String roomId);
}
