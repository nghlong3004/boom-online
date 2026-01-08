package vn.nghlong3004.boom.online.server.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bomber")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  @Column(name = "password_hash", nullable = true)
  private String passwordHash;

  @Column(name = "display_name")
  private String displayName;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "role")
  private Role role;

  @Column(name = "google_id", unique = true)
  private String googleId;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "auth_provider")
  @Builder.Default
  private AuthProvider authProvider = AuthProvider.LOCAL;

  private String birthday;
  private Integer gender;
  private Timestamp created;
  private Timestamp updated;
}
