package vn.nghlong3004.boom.online.client.model.bomber;

import lombok.AllArgsConstructor;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@AllArgsConstructor
public enum BomberType {
  BOZ(0),
  EVIE(1),
  IKE(2),
  PLUNK(3);
  public final int id;

  public String getAssetKey() {
    return this.name().toLowerCase();
  }
}
