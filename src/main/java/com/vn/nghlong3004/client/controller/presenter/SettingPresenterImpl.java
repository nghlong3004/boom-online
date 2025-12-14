package com.vn.nghlong3004.client.controller.presenter;

import com.vn.nghlong3004.client.controller.SettingPresenter;
import com.vn.nghlong3004.client.controller.SettingView;
import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public class SettingPresenterImpl implements SettingPresenter {

  private final SettingView view;

  public SettingPresenterImpl(SettingView view) {
    this.view = view;
  }

  @Override
  public void loadInitialSettings() {
    view.setMusicSelected(false);
    view.setSfxSelected(true);
    view.setVolumeValue(50);
    view.setContinueButtonVisible(false);
  }

  @Override
  public void onVolumeChanged(int volume) {
    System.out.println("Volume logic processed: " + volume);
  }

  @Override
  public void onMusicToggled(boolean isSelected) {
    String key = isSelected ? "audio_music_on" : "audio_music_off";
    view.showNotification(key, Toast.Type.INFO);
  }

  @Override
  public void onSfxToggled(boolean isSelected) {
    String key = isSelected ? "audio_sfx_on" : "audio_sfx_off";
    view.showNotification(key, Toast.Type.INFO);
  }

  @Override
  public void onBackClicked() {
    view.closeSettingModal();
  }

  @Override
  public void onContinueClicked() {
    view.closeSettingModal();
  }
}
