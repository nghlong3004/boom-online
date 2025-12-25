package vn.nghlong3004.boom.online.client.core;

import com.google.gson.*;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import vn.nghlong3004.boom.online.client.configuration.ApplicationConfiguration;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.impl.HttpServiceImpl;
import vn.nghlong3004.boom.online.client.service.impl.InMemoryRoomServiceImpl;
import vn.nghlong3004.boom.online.client.service.impl.RoomServiceImpl;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public class GameObjectContainer {

  private static final ApplicationConfiguration APPLICATION_CONFIGURATION =
      ApplicationConfiguration.getInstance();
  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(
              Instant.class,
              (JsonDeserializer<Instant>)
                  (json, typeOfT, context) -> Instant.parse(json.getAsString()))
          .registerTypeAdapter(
              Instant.class,
              (JsonSerializer<Instant>)
                  (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
          .create();
  ;
  private static final HttpService HTTP_SERVICE =
      new HttpServiceImpl(
          HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(),
          GSON,
          APPLICATION_CONFIGURATION.getServerUrl());

  private static final RoomService ONLINE_ROOM_SERVICE =
      new RoomServiceImpl(new ConcurrentHashMap<>(), HTTP_SERVICE);
  private static final RoomService OFFLINE_ROOM_SERVICE = new InMemoryRoomServiceImpl();

  public static HttpService getHttpService() {
    return HTTP_SERVICE;
  }

  public static Gson getGson() {
    return GSON;
  }

  public static RoomService getOnlineRoomService() {
    return ONLINE_ROOM_SERVICE;
  }

  public static RoomService getOfflineRoomService() {
    return OFFLINE_ROOM_SERVICE;
  }

  private GameObjectContainer() {}
}
