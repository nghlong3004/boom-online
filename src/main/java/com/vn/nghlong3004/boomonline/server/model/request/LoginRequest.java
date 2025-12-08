package com.vn.nghlong3004.boomonline.server.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public record LoginRequest(@Email String email, @NotBlank String password) {}
