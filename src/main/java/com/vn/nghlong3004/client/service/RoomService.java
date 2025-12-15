package com.vn.nghlong3004.client.service;

import com.vn.nghlong3004.client.model.room.ChatMessage;
import com.vn.nghlong3004.client.model.room.RoomMode;
import com.vn.nghlong3004.client.model.room.RoomState;
import com.vn.nghlong3004.client.model.room.RoomSummary;
import java.util.List;

public interface RoomService {
  List<RoomSummary> listRooms();

  RoomState createRoom(String hostName, RoomMode mode);

  RoomState joinRoom(String roomId, String playerName);

  RoomState getRoom(String roomId);

  RoomState leaveRoom(String roomId, String playerName);

  RoomState toggleReady(String roomId, String playerName);

  RoomState selectCharacter(String roomId, String playerName, int characterIndex);

  RoomState selectMap(String roomId, int mapIndex);

  RoomState addChat(String roomId, ChatMessage message);
}
