package com.vn.nghlong3004.client.domain.character;

/**
 * Project: boom-online-client
 *
 * Domain identifier for selectable characters.
 *
 * @author nghlong3004
 * @since 12/15/2025
 */
public enum CharacterId {
  BOZ,
  EVIE,
  IKE,
  PLUNK;

  public static CharacterId fromIndex(int index) {
    CharacterId[] values = values();
    return values[Math.floorMod(index, values.length)];
  }

  public int toIndex() {
    return ordinal();
  }
}
