package vn.nghlong3004.boom.online.server.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public enum Role implements GrantedAuthority {
  USER,
  ADMIN;

  @Override
  public String getAuthority() {
    return "ROLE_" + name();
  }
}
