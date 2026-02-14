package vn.nghlong3004.boom.online.client.model;

import lombok.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Getter
@Setter
@Builder
public class User {
  private Long id;
  private String email;
  private String displayName;
  private Role role;
}
