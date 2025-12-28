package vn.nghlong3004.boom.online.client.session;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vn.nghlong3004.boom.online.client.model.map.GameMap;
import vn.nghlong3004.boom.online.client.model.map.MapType;
import vn.nghlong3004.boom.online.client.model.room.PlayerSlot;
import vn.nghlong3004.boom.online.client.model.room.Room;

@Getter
@Setter
public class PlayingSession {

    private Room room;
    private GameMap gameMap;
    private List<PlayerSlot> players;
    private boolean gameRunning;

    private PlayingSession() {
    }

    public static PlayingSession getInstance() {
        return Holder.INSTANCE;
    }

    public void initFromRoom(Room room) {
        this.room = room;
        this.players = room.getSlots();
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

    private static class Holder {
        private static final PlayingSession INSTANCE = new PlayingSession();
    }
}
