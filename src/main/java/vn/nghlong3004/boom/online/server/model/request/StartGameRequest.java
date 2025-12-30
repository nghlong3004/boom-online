package vn.nghlong3004.boom.online.server.model.request;

import java.util.Map;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public record StartGameRequest(Map<String, String> players) {}
