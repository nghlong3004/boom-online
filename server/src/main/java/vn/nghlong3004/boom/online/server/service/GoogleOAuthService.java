package vn.nghlong3004.boom.online.server.service;

import vn.nghlong3004.boom.online.server.model.response.GoogleOAuthInitResponse;
import vn.nghlong3004.boom.online.server.model.response.GoogleOAuthStatusResponse;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
public interface GoogleOAuthService {
  GoogleOAuthInitResponse initializeAuth();

  String handleCallback(String code, String state);

  GoogleOAuthStatusResponse checkStatus(String sessionId);
}
