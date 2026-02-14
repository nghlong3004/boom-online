package vn.nghlong3004.boom.online.client.model.request;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public record ResetPasswordRequest(String token, String email, String newPassword, String lang) {}
