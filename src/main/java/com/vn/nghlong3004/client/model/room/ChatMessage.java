package com.vn.nghlong3004.client.model.room;

import java.time.Instant;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public record ChatMessage(
    ChatMessageType type,
    String senderName,
    String content,
    boolean localSender,
    Instant createdAt) {

  public static ChatMessage system(String content) {
    return new ChatMessage(ChatMessageType.SYSTEM, null, content, false, Instant.now());
  }

  public static ChatMessage player(String senderName, String content, boolean localSender) {
    return new ChatMessage(ChatMessageType.PLAYER, senderName, content, localSender, Instant.now());
  }
}
