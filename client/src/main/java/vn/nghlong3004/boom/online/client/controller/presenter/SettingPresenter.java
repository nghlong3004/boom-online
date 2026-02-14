package vn.nghlong3004.boom.online.client.controller.presenter;

import lombok.RequiredArgsConstructor;
import raven.modal.Toast;
import vn.nghlong3004.boom.online.client.controller.view.SettingPanel;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
@RequiredArgsConstructor
public class SettingPresenter {

  private final SettingPanel view;

  public void loadInitialSettings() {
    view.setMusicSelected(false);
    view.setSfxSelected(true);
    view.setVolumeValue(50);
    view.setContinueButtonVisible(false);
  }

  public void onVolumeChanged(int volume) {
    System.out.println("Volume logic processed: " + volume);
  }

  public void onMusicToggled(boolean isSelected) {
    String key = isSelected ? "audio.status.on" : "audio.status.off";
    view.showNotification(key, Toast.Type.INFO);
  }

  public void onSfxToggled(boolean isSelected) {
    String key = isSelected ? "audio.status.on" : "audio.status.off";
    view.showNotification(key, Toast.Type.INFO);
  }

  public void onBackClicked() {
    view.closeSettingModal();
  }

  public void onContinueClicked() {
    view.closeSettingModal();
  }
}
