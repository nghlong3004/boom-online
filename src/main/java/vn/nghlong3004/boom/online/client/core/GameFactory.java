package vn.nghlong3004.boom.online.client.core;

import vn.nghlong3004.boom.online.client.core.state.GameStateFactory;
import vn.nghlong3004.boom.online.client.core.state.GameStateType;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public final class GameFactory {

  public static GameCanvas createGameCanvas() {
    GameContext gameContext = createGameContext();
    GamePanel gamePanel = createGamePanel(gameContext);
    gameContext.setStateMap(GameStateFactory.createStateMap(gamePanel));
    gameContext.changeState(GameStateType.WELCOME);
    GameLoop gameLoop = createGameLoop(gamePanel);
    GameCanvas gameCanvas = new GameCanvas(new Thread(gameLoop));
    gamePanel.setting();
    gameCanvas.configure(gamePanel);
    gamePanel.requestFocus();
    return gameCanvas;
  }

  private static GameContext createGameContext() {
    return GameContext.getInstance();
  }

  private static GamePanel createGamePanel(GameContext gameContext) {
    return new GamePanel(gameContext);
  }

  private static GameLoop createGameLoop(GamePanel gamePanel) {
    return new GameLoop(gamePanel);
  }
}
