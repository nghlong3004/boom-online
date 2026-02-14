package vn.nghlong3004.boom.online.server.model.response;

import vn.nghlong3004.boom.online.server.model.GoogleAuthStatus;
import vn.nghlong3004.boom.online.server.model.User;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
public record GoogleOAuthStatusResponse(
    GoogleAuthStatus status, String accessToken, String refreshToken, User user) {}
