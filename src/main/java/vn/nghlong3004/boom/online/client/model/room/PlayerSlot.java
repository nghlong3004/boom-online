package vn.nghlong3004.boom.online.client.model.room;

import lombok.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerSlot {
  private int index;
  private boolean occupied;
  private boolean bot;
  private Long userId;
  private String displayName;
  private boolean host;
  private boolean ready;
  private int characterIndex;
}
