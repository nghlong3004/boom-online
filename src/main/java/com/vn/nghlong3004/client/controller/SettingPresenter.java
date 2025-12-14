package com.vn.nghlong3004.client.controller;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public interface SettingPresenter {
  void loadInitialSettings();

  void onVolumeChanged(int volume);

  void onMusicToggled(boolean isSelected);

  void onSfxToggled(boolean isSelected);

  void onBackClicked();

  void onContinueClicked();
}
