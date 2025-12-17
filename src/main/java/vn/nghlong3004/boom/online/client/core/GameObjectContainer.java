package vn.nghlong3004.boom.online.client.core;

import com.google.gson.Gson;
import java.net.http.HttpClient;
import java.time.Duration;
import vn.nghlong3004.boom.online.client.configuration.ApplicationConfiguration;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.service.impl.HttpServiceImpl;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public class GameObjectContainer {

  private static final ApplicationConfiguration APPLICATION_CONFIGURATION =
      ApplicationConfiguration.getInstance();
  private static final Gson GSON = new Gson();
  private static final HttpService HTTP_SERVICE =
      new HttpServiceImpl(
          HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(),
          GSON,
          APPLICATION_CONFIGURATION.getServerUrl());

  public static HttpService getHttpService() {
    return HTTP_SERVICE;
  }

  public static ApplicationConfiguration getApplicationConfiguration() {
    return APPLICATION_CONFIGURATION;
  }

  public static Gson getGson() {
    return GSON;
  }

  private GameObjectContainer() {}
}
