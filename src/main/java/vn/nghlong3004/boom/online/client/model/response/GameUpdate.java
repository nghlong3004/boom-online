package vn.nghlong3004.boom.online.client.model.response;

import vn.nghlong3004.boom.online.client.model.game.GameActionType;

public record GameUpdate(
        GameActionType type,
        Object data,
        String playerId,
        long timestamp) {
}
