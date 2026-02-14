package vn.nghlong3004.boom.online.server.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/25/2025
 */
public record CreateRoomRequest(
    @NotBlank(message = "Room name cannot be empty") @Size(max = 50, message = "Room name too long")
        String name,
    int mapIndex) {}
