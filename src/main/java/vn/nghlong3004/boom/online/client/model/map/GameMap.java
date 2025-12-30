package vn.nghlong3004.boom.online.client.model.map;

import lombok.Getter;
import vn.nghlong3004.boom.online.client.loader.MapLoader;

@Getter
public class GameMap {

    private final MapType mapType;
    private final int[][] tileData;
    private final int rows;
    private final int cols;

    public GameMap(MapType mapType) {
        this.mapType = mapType;
        this.tileData = MapLoader.loadMapFromFilePath(mapType.getAssetKey());
        this.rows = tileData.length;
        this.cols = tileData[0].length;
    }

    public int getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return TileType.STONE.id;
        }
        return tileData[row][col];
    }

    public boolean isWalkable(int row, int col) {
        int tile = getTile(row, col);
        return tile == TileType.FLOOR.id;
    }

    public boolean isSolidWall(int row, int col) {
        return getTile(row, col) == TileType.STONE.id;
    }

    public boolean isBreakableWall(int row, int col) {
        int tile = getTile(row, col);
        return tile == TileType.BRICK.id || tile == TileType.GIFT_BOX.id;
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void destroyBrick(int row, int col) {
        if (isInBounds(row, col) && isBreakableWall(row, col)) {
            tileData[row][col] = TileType.FLOOR.id;
        }
    }
}
