package com.vn.nghlong3004.boomonline.server.model;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Getter
@Builder
public class AuthenticatedUser implements UserDetails {
  private final Long id;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

  public String getAuthority() {
    return authorities.stream().findFirst().map(GrantedAuthority::getAuthority).orElse(null);
  }

  public boolean isAdmin() {
    return this.getAuthority().equals(Role.ADMIN.getAuthority());
  }

  public boolean isOwner(Long id) {
    return this.getId().equals(id);
  }
}
