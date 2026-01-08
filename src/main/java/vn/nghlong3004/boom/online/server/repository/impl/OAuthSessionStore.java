package vn.nghlong3004.boom.online.server.repository.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import vn.nghlong3004.boom.online.server.model.GoogleAuthStatus;
import vn.nghlong3004.boom.online.server.model.OAuthSession;
import vn.nghlong3004.boom.online.server.repository.OAuthSessionRepository;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
@Slf4j
@Repository
public class OAuthSessionStore implements OAuthSessionRepository {

  private static final Duration SESSION_TTL = Duration.ofMinutes(5);
  private static final int MAX_SESSIONS = 100;

  private final Map<String, OAuthSession> sessionById = new ConcurrentHashMap<>();
  private final Map<String, String> stateToSessionId = new ConcurrentHashMap<>();

  @Override
  public OAuthSession create() {
    cleanupExpired();

    if (sessionById.size() >= MAX_SESSIONS) {
      throw new IllegalStateException("Too many pending OAuth sessions");
    }

    OAuthSession session =
        OAuthSession.builder()
            .sessionId(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .status(GoogleAuthStatus.PENDING)
            .expiresAt(Instant.now().plus(SESSION_TTL))
            .build();

    sessionById.put(session.getSessionId(), session);
    stateToSessionId.put(session.getState(), session.getSessionId());

    log.debug("Created OAuth session: {}", session.getSessionId());
    return session;
  }

  @Override
  public OAuthSession findBySessionId(String sessionId) {
    OAuthSession session = sessionById.get(sessionId);
    if (session != null && session.isExpired()) {
      remove(sessionId);
      return null;
    }
    return session;
  }

  @Override
  public OAuthSession findByState(String state) {
    String sessionId = stateToSessionId.get(state);
    return sessionId != null ? findBySessionId(sessionId) : null;
  }

  @Override
  public void complete(String sessionId, String accessToken, String refreshToken, Long userId) {
    OAuthSession session = sessionById.get(sessionId);
    if (session != null) {
      session.setStatus(GoogleAuthStatus.SUCCESS);
      session.setAccessToken(accessToken);
      session.setRefreshToken(refreshToken);
      session.setUserId(userId);
      log.info("OAuth session completed: {}", sessionId);
    }
  }

  @Override
  public void remove(String sessionId) {
    OAuthSession session = sessionById.remove(sessionId);
    if (session != null) {
      stateToSessionId.remove(session.getState());
    }
  }

  @Override
  @Scheduled(fixedRate = 60000)
  public void cleanupExpired() {
    int before = sessionById.size();
    sessionById.entrySet().removeIf(e -> e.getValue().isExpired());
    stateToSessionId.entrySet().removeIf(e -> !sessionById.containsKey(e.getValue()));
    int removed = before - sessionById.size();
    if (removed > 0) {
      log.debug("Cleaned up {} expired OAuth sessions", removed);
    }
  }
}
