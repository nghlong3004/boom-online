package vn.nghlong3004.boom.online.server.service;

import org.springframework.security.core.Authentication;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public interface TokenService {
  String generateAccessToken(Authentication authentication);

  String generateRefreshToken(Authentication authentication);

  String getUsernameFromToken(String token);

  void validateAccessToken(String token);

  void validateRefreshToken(String token);

  String getRole(String token);
}
