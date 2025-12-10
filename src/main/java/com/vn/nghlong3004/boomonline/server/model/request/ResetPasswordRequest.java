package com.vn.nghlong3004.boomonline.server.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
public record ResetPasswordRequest(
    @NotBlank(message = "Token is required") String token,
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @Size(min = 6, message = "Password must be at least 6 characters") String newPassword,
    @NotBlank(message = "Language is required") String lang) {}
