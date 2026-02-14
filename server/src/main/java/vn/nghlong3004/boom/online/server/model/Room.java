package vn.nghlong3004.boom.online.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import lombok.*;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/24/2025
 */
@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
  @Id private String id;

  private String name;

  @Column(name = "owner_id")
  private Long ownerId;

  @Column(name = "owner_display_name")
  private String ownerDisplayName;

  @Column(name = "map_index")
  private int mapIndex;

  @Enumerated(EnumType.STRING)
  private RoomStatus status;

  @Column(name = "max_players")
  private int maxPlayers;

  private Instant created;

  @OneToMany(
      mappedBy = "room",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  @OrderBy("index ASC")
  private List<PlayerSlot> slots;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatMessage> chat;
}
