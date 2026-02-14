package vn.nghlong3004.boom.online.client.model.request;

import vn.nghlong3004.boom.online.client.model.game.GameActionType;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public record GameActionRequest(GameActionType type, Object data, String playerId) {}
