package vn.nghlong3004.boom.online.client.model.room;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Getter
@Builder
public class ChatMessage {
  private final String id;
  private final ChatMessageType type;

  private final Long senderId;
  private final String senderDisplayName;

  private final String content;
  private final Instant created;
}
