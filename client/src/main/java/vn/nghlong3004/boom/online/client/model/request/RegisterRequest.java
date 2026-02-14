package vn.nghlong3004.boom.online.client.model.request;

import lombok.Builder;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Builder
public record RegisterRequest(
    String email, String password, String birthday, String displayName, Integer gender) {}
