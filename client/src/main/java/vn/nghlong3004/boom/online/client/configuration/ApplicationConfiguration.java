package vn.nghlong3004.boom.online.client.configuration;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/16/2025
 */
@Slf4j
@Getter
public class ApplicationConfiguration {

  private static final String NAME_FILE_CONFIGURATION = "application.properties";

  private final Properties properties;

  private final String serverUrl;
  private final int fps;
  private final int ups;

  private final int originalTileSize;
  private final int maxScreenCol;
  private final int maxScreenRow;
  private final float scale;

  private final int tileSize;
  private final int screenWidth;
  private final int screenHeight;

  private ApplicationConfiguration() {
    this.properties = new Properties();
    loadConfiguration();
    loadEnvironment();

    this.serverUrl = getProperty("application.server.url");

    this.fps = parseInt("application.game.fps");
    this.ups = parseInt("application.game.ups");

    this.originalTileSize = parseInt("application.game.original_tile_size");
    this.maxScreenCol = parseInt("application.game.max_screen_column");
    this.maxScreenRow = parseInt("application.game.max_screen_row");
    this.scale = parseFloat("application.game.scale");

    this.tileSize = (int) (originalTileSize * scale);
    this.screenWidth = tileSize * maxScreenCol;
    this.screenHeight = tileSize * maxScreenRow;

    log.info("Config Loaded");
  }

  public static ApplicationConfiguration getInstance() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    private static final ApplicationConfiguration INSTANCE = new ApplicationConfiguration();
  }

  private void loadConfiguration() {
    try (InputStream inputStream =
        getClass().getClassLoader().getResourceAsStream(NAME_FILE_CONFIGURATION)) {
      if (inputStream != null) {
        properties.load(inputStream);
      } else {
        log.error("CRITICAL: {} not found in classpath!", NAME_FILE_CONFIGURATION);
      }
    } catch (Exception e) {
      log.error("Error loading configuration", e);
    }
  }

  private void loadEnvironment() {
    for (String key : properties.stringPropertyNames()) {
      String originalValue = properties.getProperty(key);
      String resolvedValue = resolvePlaceholders(originalValue);
      properties.setProperty(key, resolvedValue);
    }
  }

  private String resolvePlaceholders(String text) {
    if (text == null) return null;

    Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
    Matcher matcher = pattern.matcher(text);
    StringBuilder buffer = new StringBuilder();

    while (matcher.find()) {
      String varName = matcher.group(1);
      String replacement = System.getProperty(varName);
      if (replacement == null) {
        replacement = System.getenv(varName);
      }
      if (replacement == null) {
        System.out.println("Warning: Could not resolve variable " + varName);
        replacement = "${" + varName + "}";
      }

      matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(buffer);
    return buffer.toString();
  }

  private String getProperty(String key) {
    String value = properties.getProperty(key);
    return value.trim();
  }

  private int parseInt(String key) {
    String value = getProperty(key);
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      log.warn("Invalid integer for key '{}': {}.", key, value);
      throw new NumberFormatException(e.getMessage());
    }
  }

  private float parseFloat(String key) {
    String value = getProperty(key);
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException e) {
      log.warn("Invalid float for key '{}': {}", key, value);
      throw new NumberFormatException(e.getMessage());
    }
  }
}
