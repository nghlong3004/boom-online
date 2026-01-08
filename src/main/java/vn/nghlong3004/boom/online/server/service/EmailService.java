package vn.nghlong3004.boom.online.server.service;

import java.util.Map;
import vn.nghlong3004.boom.online.server.email.TemplateType;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
public interface EmailService {
  void sendHtmlEmail(String to, String lang, TemplateType type, Map<String, String> data);
}
