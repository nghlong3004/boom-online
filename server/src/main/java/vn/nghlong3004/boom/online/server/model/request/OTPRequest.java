package vn.nghlong3004.boom.online.server.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public record OTPRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @Size(min = 8, max = 8, message = "OTP equals 8 characters") String OTP) {}
