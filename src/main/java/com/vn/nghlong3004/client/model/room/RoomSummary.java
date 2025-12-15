package com.vn.nghlong3004.client.model.room;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public record RoomSummary(String id, String name, RoomMode mode, int currentPlayers, int maxPlayers) {}
