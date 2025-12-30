package vn.nghlong3004.boom.online.client.constant;

import java.awt.Color;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public final class PlayingConstant {

  public static final int MAP_COLUMNS = 21;
  public static final int MAP_ROWS = 13;

  public static final int HUD_COLUMNS = 4;
  public static final int HUD_WIDTH = HUD_COLUMNS * GameConstant.TILE_SIZE;

  public static final int MAP_WIDTH = MAP_COLUMNS * GameConstant.TILE_SIZE;
  public static final int MAP_HEIGHT = MAP_ROWS * GameConstant.TILE_SIZE;

  public static final int HUD_X = MAP_WIDTH;
  public static final int HUD_Y = 0;

  public static final int MAX_PLAYERS = 4;

  public static final int PLAYER_CARD_HEIGHT = GameConstant.GAME_HEIGHT / MAX_PLAYERS;
  public static final int PLAYER_CARD_MARGIN = 6;
  public static final int PLAYER_CARD_PADDING = 10;
  public static final int PLAYER_CARD_ARC = 16;

  public static final int AVATAR_SIZE = 64;
  public static final int AVATAR_BORDER_SIZE = 3;

  public static final int HEALTH_BAR_WIDTH = 80;
  public static final int HEALTH_BAR_HEIGHT = 8;

  public static final int TIMER_HEIGHT = 50;
  public static final int TIMER_MARGIN = 10;

  public static final Color[] PLAYER_COLORS = {
    new Color(66, 133, 244), new Color(234, 67, 53), new Color(52, 168, 83), new Color(251, 188, 5)
  };

  private PlayingConstant() {}
}
