package vn.nghlong3004.boom.online.client.constant;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public final class GameConstant {

  public static final String TITLE = "Boom Online";
  public static final String DISPLAY_NAME_DEFAULT = "Guest";

  public static final int MAX_SCREEN_ROW = 13;
  public static final int MAX_SCREEN_COLUMN = 25;
  public static final int ORIGINAL_TILE_SIZE = 32;
  public static final float SCALE = 1.5f;
  public static final int TILE_SIZE = (int) (ORIGINAL_TILE_SIZE * SCALE);
  public static final int GAME_WIDTH = TILE_SIZE * MAX_SCREEN_COLUMN;
  public static final int GAME_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;
}
