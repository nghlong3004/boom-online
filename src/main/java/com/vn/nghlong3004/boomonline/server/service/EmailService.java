package com.vn.nghlong3004.boomonline.server.service;

import com.vn.nghlong3004.boomonline.server.service.email.EmailType;
import java.util.Map;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
public interface EmailService {
  void sendHtmlEmail(String to, String lang, EmailType type, Map<String, String> data);
}
