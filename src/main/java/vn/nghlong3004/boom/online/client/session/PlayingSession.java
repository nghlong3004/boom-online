package vn.nghlong3004.boom.online.client.session;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.model.map.GameMap;
import vn.nghlong3004.boom.online.client.model.map.MapType;
import vn.nghlong3004.boom.online.client.model.playing.PlayerInfo;
import vn.nghlong3004.boom.online.client.model.room.PlayerSlot;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.util.ImageUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Getter
@Setter
public class PlayingSession {

  private Room room;
  private GameMap gameMap;
  private List<PlayerInfo> players;
  private boolean gameRunning;

  private PlayingSession() {}

  public static PlayingSession getInstance() {
    return Holder.INSTANCE;
  }

  public void initFromRoom(Room room) {
    this.room = room;
    this.players = createPlayersFromSlots(room.getSlots());
    int mapIndex = Math.floorMod(room.getMapIndex(), MapType.values().length);
    this.gameMap = new GameMap(MapType.values()[mapIndex]);
    this.gameRunning = true;
  }

  public void clear() {
    this.room = null;
    this.gameMap = null;
    this.players = null;
    this.gameRunning = false;
  }

  private List<PlayerInfo> createPlayersFromSlots(List<PlayerSlot> slots) {
    List<PlayerInfo> playerInfos = new ArrayList<>();
    if (slots == null) {
      return playerInfos;
    }

    for (PlayerSlot slot : slots) {
      if (slot != null && slot.isOccupied()) {
        playerInfos.add(createPlayerInfo(slot));
      }
    }
    return playerInfos;
  }

  private PlayerInfo createPlayerInfo(PlayerSlot slot) {
    int safeCharacterIndex =
        Math.floorMod(slot.getCharacterIndex(), RoomConstant.PLAYER_AVATARS.length);
    BufferedImage avatar = loadAvatar(safeCharacterIndex);
    return PlayerInfo.builder()
        .slotIndex(slot.getIndex())
        .userId(slot.getUserId())
        .displayName(slot.getDisplayName())
        .characterIndex(safeCharacterIndex)
        .host(slot.isHost())
        .avatar(avatar)
        .build();
  }

  private BufferedImage loadAvatar(int characterIndex) {
    int index = Math.floorMod(characterIndex, RoomConstant.PLAYER_AVATARS.length);
    return ImageUtil.loadImage(RoomConstant.PLAYER_AVATARS[index]);
  }

  private static class Holder {
    private static final PlayingSession INSTANCE = new PlayingSession();
  }
}
