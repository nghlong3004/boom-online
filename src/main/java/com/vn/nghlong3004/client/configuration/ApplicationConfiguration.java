package com.vn.nghlong3004.client.configuration;

import com.google.gson.Gson;
import com.vn.nghlong3004.client.constant.GameConstant;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@Slf4j
public final class ApplicationConfiguration {
  private int fps = 0;
  private int ups = 0;

  @Getter private final String loginId;

  @Getter private final Gson gson;

  private final Properties properties;

  @Getter @Setter private String accessToken;

  @Getter @Setter private String refreshToken;

  public static ApplicationConfiguration getInstance() {
    return Holder.INSTANCE;
  }

  private ApplicationConfiguration() {
    properties = new Properties();
    gson = new Gson();
    loginId = UUID.randomUUID().toString();
    try (InputStream inputStream =
        ApplicationConfiguration.class.getResourceAsStream(GameConstant.APPLICATION_PATH)) {
      if (inputStream == null) {
        String msg = GameConstant.APPLICATION_PATH + " not found in resource folder";
        log.error(msg);
        throw new IOException(msg);
      }
      properties.load(inputStream);
      log.debug("Read properties from {}", GameConstant.APPLICATION_PATH);
    } catch (IOException e) {
      log.error("{} error close file: message {}", GameConstant.APPLICATION_PATH, e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public String getServerUrl() {
    return getPropertyValue("application.server.url");
  }

  public int getFps() {
    if (fps == 0) {
      fps = Integer.parseInt(getPropertyValue("application.game.fps"));
    }
    return fps;
  }

  public int getUps() {
    if (ups == 0) {
      ups = Integer.parseInt(getPropertyValue("application.game.ups"));
    }
    return ups;
  }

  public int getOriginalTileSize() {
    return Integer.parseInt(getPropertyValue("application.game.original_tile_size"));
  }

  public int getMaxScreenColumn() {
    return Integer.parseInt(getPropertyValue("application.game.max_screen_column"));
  }

  public int getMaxScreenRow() {
    return Integer.parseInt(getPropertyValue("application.game.max_screen_row"));
  }

  public float getScale() {
    return Float.parseFloat(getPropertyValue("application.game.scale"));
  }

  private String getPropertyValue(String key) {
    String value = properties.getProperty(key);
    if (value == null || value.isBlank()) {
      value = "";
      log.warn("Missing property: {} ", key);
    }
    return value;
  }

  private static class Holder {
    private static final ApplicationConfiguration INSTANCE = new ApplicationConfiguration();
  }
}
