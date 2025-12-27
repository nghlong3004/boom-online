package vn.nghlong3004.boom.online.server.interceptor;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import vn.nghlong3004.boom.online.server.service.TokenService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

  private final TokenService tokenService;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    assert accessor != null;
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      List<String> authorization = accessor.getNativeHeader("Authorization");
      if (authorization != null && !authorization.isEmpty()) {
        String token = authorization.getFirst().substring(7);
        tokenService.validateAccessToken(token);
        authenticateUser(accessor, token);
      } else {
        log.warn("WebSocket connection attempt without Authorization header");
      }
    }
    return message;
  }

  private void authenticateUser(StompHeaderAccessor accessor, String token) {
    String username = tokenService.getUsernameFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    accessor.setUser(authentication);
    log.info("User '{}' connected via WebSocket", username);
  }
}
