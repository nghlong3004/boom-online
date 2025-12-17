package vn.nghlong3004.boom.online.client.model.response;

import vn.nghlong3004.boom.online.client.model.User;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public record LoginResponse(String accessToken, String refreshToken, User user) {}
