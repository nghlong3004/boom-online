package com.vn.nghlong3004.client.model.request;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public record ResetPasswordRequest(String token, String email, String newPassword, String lang) {}
