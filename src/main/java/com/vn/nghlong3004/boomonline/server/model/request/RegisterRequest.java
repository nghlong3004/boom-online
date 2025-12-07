package com.vn.nghlong3004.boomonline.server.model.request;

import jakarta.annotation.Nonnull;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public record RegisterRequest(
    @Nonnull String email,
    @Nonnull String password,
    @Nonnull String birthday,
    @Nonnull String fullName,
    @Nonnull Integer gender) {}
