package vn.nghlong3004.boom.online.client.session;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
@Slf4j
public class SessionHandlerAdapter extends StompSessionHandlerAdapter {
  @Override
  public void handleException(
      @NonNull StompSession session,
      StompCommand command,
      @NonNull StompHeaders headers,
      byte @NonNull [] payload,
      @NonNull Throwable exception) {
    log.error("Stomp protocol error", exception);
  }

  @Override
  public void handleTransportError(@NonNull StompSession session, @NonNull Throwable exception) {
    log.error("Transport error (connection lost)", exception);
  }
}
