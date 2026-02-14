package vn.nghlong3004.boom.online.server.model.request;

import vn.nghlong3004.boom.online.server.model.GameActionType;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public record GameActionRequest(GameActionType type, Object data, String playerId) {}
