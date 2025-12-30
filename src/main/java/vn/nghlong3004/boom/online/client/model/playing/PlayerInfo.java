package vn.nghlong3004.boom.online.client.model.playing;

import java.awt.image.BufferedImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerInfo {

  private final int slotIndex;
  private final Long userId;
  private final String displayName;
  private final int characterIndex;
  private final boolean host;
  private final BufferedImage avatar;

  @Builder.Default private int lives = 1;

  @Builder.Default private boolean alive = true;
}
