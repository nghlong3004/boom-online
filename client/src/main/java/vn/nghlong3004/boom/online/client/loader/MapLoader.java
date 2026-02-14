package vn.nghlong3004.boom.online.client.loader;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.MapConstant;
import vn.nghlong3004.boom.online.client.model.map.MapType;
import vn.nghlong3004.boom.online.client.model.map.TileType;
import vn.nghlong3004.boom.online.client.util.ImageUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Slf4j
public class MapLoader {
  private static final String SPACE = "\\s+";

  public static int[][] loadMapFromFilePath(String name) {
    String filePath = "/map_data/" + name + ".txt";
    String raw = FileLoader.loadFile(filePath);
    // 0 1 2 3 4 10 11 -> [0, 1, 2, 3, 4, 10, 11] map String to int[][]
    return raw.lines()
        .map(line -> Arrays.stream(line.trim().split(SPACE)).mapToInt(Integer::parseInt).toArray())
        .toArray(int[][]::new);
  }

  public static BufferedImage[][] loadMapAssets() {
    log.info("Loading title sprites");
    var data = new BufferedImage[5][4];
    for (MapType row : MapType.values()) {
      for (TileType column : TileType.values()) {
        data[row.id][column.id] = loadTile(row, column);
      }
    }
    return data;
  }

  private static BufferedImage loadTile(MapType row, TileType column) {
    String path =
        MapConstant.IMAGE_PATH_TEMPLATE.formatted(row.getAssetKey(), column.getAssetKey());
    return ImageUtil.loadImage(path);
  }

  private MapLoader() {}
}
