package vn.nghlong3004.boom.online.server.repository;

import vn.nghlong3004.boom.online.server.model.OAuthSession;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
public interface OAuthSessionRepository {
  OAuthSession create();

  OAuthSession findBySessionId(String sessionId);

  OAuthSession findByState(String state);

  void complete(String sessionId, String accessToken, String refreshToken, Long userId);

  void remove(String sessionId);

  void cleanupExpired();
}
