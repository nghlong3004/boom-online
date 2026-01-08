package vn.nghlong3004.boom.online.server.email;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
public interface LocaleStrategy {
  String getSupportedLanguage();

  String getSubject(TemplateType type);

  String getTemplatePath(TemplateType type);
}
