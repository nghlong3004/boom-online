package com.vn.nghlong3004.client.util;

import java.util.Locale;
import java.util.ResourceBundle;
import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/7/2025
 */
public class LanguageUtil {
  private ResourceBundle resourceBundle;
  @Getter private Locale currentLocale;

  private LanguageUtil() {
    loadResource("vi");
  }

  public static LanguageUtil getInstance() {
    return HOLDER.INSTANCE;
  }

  public void changeLanguage(String languageCode) {
    loadResource(languageCode);
  }

  private void loadResource(String languageCode) {
    Locale locale = Locale.forLanguageTag(languageCode);
    this.currentLocale = locale;
    this.resourceBundle = ResourceBundle.getBundle("messages", locale);
  }

  public String getString(String key) {
    try {
      return resourceBundle.getString(key);
    } catch (Exception e) {
      return "Key not found: " + key;
    }
  }

  private static class HOLDER {
    private static final LanguageUtil INSTANCE = new LanguageUtil();
  }
}
