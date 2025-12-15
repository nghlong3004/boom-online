package com.vn.nghlong3004.client.model.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSlot {
  private int index;
  private String playerName;
  private boolean host;
  private boolean ready;
  private int characterIndex;
  private boolean machine;

  public boolean isEmpty() {
    return playerName == null || playerName.isBlank();
  }
}
