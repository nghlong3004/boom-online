package com.vn.nghlong3004.client.controller;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public interface RoomPresenter {
  void loadRoom();

  void onBackToListClicked();

  void onToggleReadyClicked();

  void onStartClicked();

  void onCharacterPrevious();

  void onCharacterNext();

  void onMapPrevious();

  void onMapNext();

  void onSendChat(String message);
}
