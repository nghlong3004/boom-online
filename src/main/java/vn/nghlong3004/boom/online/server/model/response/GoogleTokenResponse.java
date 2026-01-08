package vn.nghlong3004.boom.online.server.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
public record GoogleTokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("id_token") String idToken,
    @JsonProperty("expires_in") int expiresIn,
    @JsonProperty("token_type") String tokenType) {}
