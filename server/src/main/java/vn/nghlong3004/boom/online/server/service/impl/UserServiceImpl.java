package vn.nghlong3004.boom.online.server.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.nghlong3004.boom.online.server.constant.JwtConstant;
import vn.nghlong3004.boom.online.server.model.AuthenticatedUser;
import vn.nghlong3004.boom.online.server.service.UserService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/25/2025
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
  @Override
  public AuthenticatedUser getCurrentUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assert authentication != null;
    Jwt jwt = (Jwt) authentication.getPrincipal();
    assert jwt != null;
    Long id = jwt.getClaim(JwtConstant.USER_ID);
    var username = authentication.getName();
    String role = jwt.getClaimAsString(JwtConstant.SCOPE);
    var authorities = List.of(new SimpleGrantedAuthority(role));
    String displayName = jwt.getClaimAsString(JwtConstant.DISPLAY_NAME);
    log.info("Get current user -> id:{}, username:{}, role:{}", id, username, role);
    return AuthenticatedUser.builder()
        .id(id)
        .username(username)
        .displayName(displayName)
        .authorities(authorities)
        .build();
  }
}
