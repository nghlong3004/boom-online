package com.vn.nghlong3004.boomonline.server.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String message, int status, String code) {}
