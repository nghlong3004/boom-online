package com.vn.nghlong3004.client.controller;

import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public interface SettingView {
  void showNotification(String messageKey, Toast.Type type);

  void closeSettingModal();

  void setMusicSelected(boolean selected);

  void setSfxSelected(boolean selected);

  void setVolumeValue(int volume);

  void setContinueButtonVisible(boolean visible);

  boolean isMusicSelected();

  boolean isSfxSelected();

  int getVolumeValue();
}
