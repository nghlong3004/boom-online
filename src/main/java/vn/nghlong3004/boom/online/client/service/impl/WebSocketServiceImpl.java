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
import vn.nghlong3004.boom.online.client.model.response.GameUpdate;
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
  private static final String WEBSOCKET = "ws://";
  private final String serverUrl;
  private final WebSocketStompClient stompClient;
  private StompSession session;
  private StompSession.Subscription currentSubscription;
  private StompSession.Subscription gameSubscription;

  public WebSocketServiceImpl(String host, Gson gson) {
    this.serverUrl = WEBSOCKET + host + "/ws";
    StandardWebSocketClient client = new StandardWebSocketClient();
    this.stompClient = new WebSocketStompClient(client);
    this.stompClient.setMessageConverter(new GsonMessageConverter(gson));
  }

  @Override
  public void connectAndSubscribe(String token, String roomId, Consumer<Room> onRoomUpdate) {
    ensureConnected(token);

    String topic = "/topic/room/" + roomId;
    currentSubscription =
        session.subscribe(
            topic,
            new StompFrameHandler() {
              @Override
              @NonNull
              public Type getPayloadType(@NonNull StompHeaders headers) {
                return Room.class;
              }

              @Override
              public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                Room room = (Room) payload;
                onRoomUpdate.accept(room);
              }
            });

    log.info("Subscribed to topic: {}", topic);
  }

  @Override
  public void subscribeToGame(String roomId, Consumer<GameUpdate> onGameUpdate) {
    if (session == null || !session.isConnected()) {
      log.warn("Cannot subscribe to game: WebSocket is not connected");
      return;
    }

    if (gameSubscription != null) {
      gameSubscription.unsubscribe();
      log.info("Unsubscribed from previous game topic");
    }

    String topic = "/topic/game/" + roomId;
    gameSubscription =
        session.subscribe(
            topic,
            new StompFrameHandler() {
              @Override
              @NonNull
              public Type getPayloadType(@NonNull StompHeaders headers) {
                return GameUpdate.class;
              }

              @Override
              public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                GameUpdate update = (GameUpdate) payload;
                onGameUpdate.accept(update);
              }
            });

    log.info("Subscribed to game topic: {}", topic);
  }

  @Override
  public void unsubscribeFromGame() {
    if (gameSubscription != null) {
      gameSubscription.unsubscribe();
      gameSubscription = null;
      log.info("Unsubscribed from game topic");
    }
  }

  private void ensureConnected(String token) {
    if (session != null && session.isConnected()) {
      return;
    }

    StompHeaders headers = new StompHeaders();
    headers.add("Authorization", "Bearer " + token);

    try {
      session =
          stompClient
              .connectAsync(
                  serverUrl, (WebSocketHttpHeaders) null, headers, new SessionHandlerAdapter())
              .get();
      log.info("WebSocket Session established: {}", session.getSessionId());
    } catch (InterruptedException | ExecutionException e) {
      log.error("WebSocket connection failed", e);
      throw new RuntimeException("Cannot connect to game server", e);
    }
  }

  @Override
  public void send(String destination, Object payload) {
    if (session != null && session.isConnected()) {
      try {
        log.info("Check destination: {}", destination);
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
    unsubscribeFromGame();
    if (currentSubscription != null) {
      currentSubscription.unsubscribe();
      currentSubscription = null;
    }
    if (session != null && session.isConnected()) {
      session.disconnect();
      session = null;
      log.info("Disconnected from WebSocket completely");
    }
  }

  @Override
  public boolean isConnected() {
    return session != null && session.isConnected();
  }
}
