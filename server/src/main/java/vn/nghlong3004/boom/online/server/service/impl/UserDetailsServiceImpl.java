package vn.nghlong3004.boom.online.server.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.nghlong3004.boom.online.server.model.AuthenticatedUser;
import vn.nghlong3004.boom.online.server.repository.UserRepository;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @NullMarked
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    var authorities = List.of(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    return AuthenticatedUser.builder()
        .username(user.getEmail())
        .password(user.getPasswordHash())
        .displayName(user.getDisplayName())
        .id(user.getId())
        .authorities(authorities)
        .build();
  }
}
