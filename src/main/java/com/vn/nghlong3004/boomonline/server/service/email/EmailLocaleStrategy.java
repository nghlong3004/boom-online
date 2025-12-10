package com.vn.nghlong3004.boomonline.server.service.email;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
public interface EmailLocaleStrategy {
  String getSupportedLanguage();

  String getSubject(EmailType type);

  String getTemplatePath(EmailType type);
}
