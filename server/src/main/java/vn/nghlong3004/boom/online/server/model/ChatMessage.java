package vn.nghlong3004.boom.online.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/24/2025
 */
@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
  @Id private String id;

  @Enumerated(EnumType.STRING)
  private ChatMessageType type;

  @Column(name = "sender_id")
  private Long senderId;

  @Column(name = "sender_display_name")
  private String senderDisplayName;

  private String content;

  private Instant created;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  @JsonIgnore
  private Room room;
}
