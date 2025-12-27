package vn.nghlong3004.boom.online.server.model.request;

import vn.nghlong3004.boom.online.server.model.RoomActionType;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
public record RoomActionRequest(RoomActionType type, Object data, String username) {}
