package com.vn.nghlong3004.boomonline.server.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Data;
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
@Table(name = "bomber")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  @Column(name = "password_hash")
  private String passwordHash;

  @Column(name = "full_name")
  private String fullName;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "role")
  private Role role;

  private String birthday;
  private Integer gender;
  private Timestamp created;
  private Timestamp updated;
}
