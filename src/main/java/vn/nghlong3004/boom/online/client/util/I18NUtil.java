package vn.nghlong3004.boom.online.client.util;

import java.util.Locale;
import java.util.ResourceBundle;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Slf4j
public class I18NUtil {
  private static ResourceBundle resourceBundle;
  private static Locale currentLocale;

  public static void changeLanguage(String languageCode) {
    loadResource(languageCode);
  }

  public static void registerDefaultLanguage() {
    loadResource("vi");
  }

  public static String getCurrentLanguage() {
    return currentLocale.getLanguage();
  }

  public static String getString(String key) {
    try {
      log.info("Loading key: {}", key);
      return resourceBundle.getString(key);
    } catch (Exception e) {
      return "Key not found: " + key;
    }
  }

  private static void loadResource(String languageCode) {
    Locale locale = Locale.forLanguageTag(languageCode);
    currentLocale = locale;
    resourceBundle = ResourceBundle.getBundle("messages", locale);
  }
}
