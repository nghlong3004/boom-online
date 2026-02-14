package vn.nghlong3004.boom.online.server.model.response;

import vn.nghlong3004.boom.online.server.model.User;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public record LoginResponse(String accessToken, String refreshToken, User user) {}
