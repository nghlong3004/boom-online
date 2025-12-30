package vn.nghlong3004.boom.online.client.core;

import com.google.gson.*;
import vn.nghlong3004.boom.online.client.configuration.ApplicationConfiguration;
import vn.nghlong3004.boom.online.client.configuration.GsonFactory;
import vn.nghlong3004.boom.online.client.service.GameService;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.service.impl.GameServiceImpl;
import vn.nghlong3004.boom.online.client.service.impl.HttpServiceImpl;
import vn.nghlong3004.boom.online.client.service.impl.InMemoryRoomServiceImpl;
import vn.nghlong3004.boom.online.client.service.impl.RoomServiceImpl;
import vn.nghlong3004.boom.online.client.service.impl.WebSocketServiceImpl;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.GameSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public class GameObjectContainer {

  private static final ApplicationConfiguration APPLICATION_CONFIGURATION =
      ApplicationConfiguration.getInstance();
  private static final Gson GSON = GsonFactory.createGson();

  private static final HttpService HTTP_SERVICE =
      new HttpServiceImpl(APPLICATION_CONFIGURATION.getServerUrl(), GSON);

  private static final WebSocketService WEB_SOCKET_SERVICE =
      new WebSocketServiceImpl(APPLICATION_CONFIGURATION.getServerUrl(), GSON);

  private static final RoomService ONLINE_ROOM_SERVICE =
      new RoomServiceImpl(HTTP_SERVICE, WEB_SOCKET_SERVICE);
  private static final RoomService OFFLINE_ROOM_SERVICE = new InMemoryRoomServiceImpl();

  private static final GameService GAME_SERVICE = new GameServiceImpl(WEB_SOCKET_SERVICE);

  static {
    GameSession.getInstance().setGameService(GAME_SERVICE);
  }

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

  public static RoomService getRoomService() {
    if (ApplicationSession.getInstance().isOfflineMode()) {
      return OFFLINE_ROOM_SERVICE;
    }
    return ONLINE_ROOM_SERVICE;
  }

  public static WebSocketService getWebSocketService() {
    return WEB_SOCKET_SERVICE;
  }

  private GameObjectContainer() {}
}
