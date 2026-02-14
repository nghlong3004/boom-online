package vn.nghlong3004.boom.online.client.core.state;

import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.core.GameLogic;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
public interface GameState extends GameLogic {
  void previous(GameContext gameContext);

  void next(GameContext gameContext);
}
