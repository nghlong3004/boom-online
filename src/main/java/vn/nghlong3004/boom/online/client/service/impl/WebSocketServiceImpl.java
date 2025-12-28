package vn.nghlong3004.boom.online.client.service.impl;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.converter.GsonMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.session.SessionHandlerAdapter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

  private final String serverUrl;
  private final WebSocketStompClient stompClient;
  private StompSession session;

  public WebSocketServiceImpl(String host, Gson gson) {
    this.serverUrl = "ws" + host + "/ws";
    StandardWebSocketClient client = new StandardWebSocketClient();
    this.stompClient = new WebSocketStompClient(client);
    this.stompClient.setMessageConverter(new GsonMessageConverter(gson));
  }

  @Override
  public void connectAndSubscribe(String token, String roomId, Consumer<Room> onRoomUpdate) {
    if (session != null && session.isConnected()) return;

    StompHeaders headers = new StompHeaders();
    headers.add("Authorization", "Bearer " + token);

    try {
      session =
          stompClient
              .connectAsync(
                  serverUrl, (WebSocketHttpHeaders) null, headers, new SessionHandlerAdapter())
              .get();

      session.subscribe(
          "/topic/room/" + roomId,
          new StompFrameHandler() {
            @Override
            @NonNull
            public Type getPayloadType(@NonNull StompHeaders headers) {
              return Room.class;
            }

            @Override
            public void handleFrame(@NonNull StompHeaders headers, Object payload) {
              Room room = (Room) payload;
              SwingUtilities.invokeLater(() -> onRoomUpdate.accept(room));
            }
          });

    } catch (InterruptedException | ExecutionException e) {
      log.error("WebSocket connection failed", e);
      throw new RuntimeException("Cannot connect to game server", e);
    }
  }

  @Override
  public void send(String destination, Object payload) {
    if (session != null && session.isConnected()) {
      try {
        session.send(destination, payload);
      } catch (Exception e) {
        log.error("Failed to send message to {}", destination, e);
      }
    } else {
      log.warn("Cannot send message: WebSocket is not connected");
    }
  }

  @Override
  public void disconnect() {
    if (session != null && session.isConnected()) {
      session.disconnect();
      session = null;
      log.info("Disconnected from WebSocket");
    }
  }
}
