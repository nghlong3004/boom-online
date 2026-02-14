package vn.nghlong3004.boom.online.client.model.request;

import vn.nghlong3004.boom.online.client.model.room.RoomActionType;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
public record RoomActionRequest(RoomActionType type, Object data, String username) {}
