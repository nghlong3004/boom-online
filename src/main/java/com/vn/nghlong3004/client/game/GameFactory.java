package com.vn.nghlong3004.client.game;

import com.vn.nghlong3004.client.context.GameContext;
import com.vn.nghlong3004.client.game.state.GameStateFactory;
import com.vn.nghlong3004.client.game.state.GameStateType;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public final class GameFactory {

  public static GameWindow createGameWindow() {
    GameContext gameContext = createGameContext();
    GamePanel gamePanel = createGamePanel(gameContext);
    gameContext.setStateMap(GameStateFactory.createStateMap(gamePanel));
    gameContext.changeState(GameStateType.WELCOME);
    GameLoop gameLoop = createGameLoop(gamePanel);
    GameWindow gameWindow = new GameWindow(new Thread(gameLoop));
    gamePanel.setting();
    gameWindow.setting(gamePanel);
    gamePanel.requestFocus();
    return gameWindow;
  }

  private static GameContext createGameContext() {
    return new GameContext();
  }

  private static GamePanel createGamePanel(GameAdapter gameContext) {
    return new GamePanel(gameContext);
  }

  private static GameLoop createGameLoop(GamePanel gamePanel) {
    return new GameLoop(gamePanel);
  }
}
