package com.vn.nghlong3004.boomonline.server.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public record RegisterRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,
    @NotNull(message = "Date of birth is required")
        @JsonFormat(pattern = "yyyy-MM-dd") // Standard JSON date format
        String birthday,
    @NotBlank(message = "Full name is required")
        @Size(max = 30, message = "Full name must not exceed 30 characters")
        String fullName,

    // (0: Male, 1: Female, 2: Other)
    @NotNull(message = "Gender is required")
        @Min(value = 0, message = "Invalid gender")
        @Max(value = 2, message = "Invalid gender")
        Integer gender) {}
