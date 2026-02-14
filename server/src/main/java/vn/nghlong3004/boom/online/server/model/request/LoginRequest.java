package vn.nghlong3004.boom.online.server.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public record LoginRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @Size(min = 6, message = "Password must be at least 6 characters") String password) {}
