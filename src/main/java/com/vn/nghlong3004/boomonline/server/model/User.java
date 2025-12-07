package com.vn.nghlong3004.boomonline.server.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Data;

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
  private String birthday;
  private Integer gender;
  private Timestamp created;
  private Timestamp updated;
}
