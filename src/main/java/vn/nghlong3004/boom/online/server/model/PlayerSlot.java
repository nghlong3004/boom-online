package vn.nghlong3004.boom.online.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/24/2025
 */
@Entity
@Table(name = "player_slot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerSlot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(name = "slot_index")
  private int index;

  private boolean occupied;

  @Column(name = "is_bot")
  private boolean bot;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "username")
  private String username;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "is_host")
  private boolean host;

  @Column(name = "is_ready")
  private boolean ready;

  @Column(name = "character_index")
  private int characterIndex;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  @JsonIgnore
  private Room room;
}
