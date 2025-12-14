package com.vn.nghlong3004.client.game.state;

import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.constant.GameConstant;
import com.vn.nghlong3004.client.context.GameContext;
import com.vn.nghlong3004.client.controller.view.CustomModalBorder;
import com.vn.nghlong3004.client.controller.view.component.TextButton;
import com.vn.nghlong3004.client.game.GamePanel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import lombok.Builder;
import raven.modal.ModalDialog;
import raven.modal.option.Option;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/13/2025
 */
@Builder
public class HomeState implements GameState {

  private final BufferedImage background;
  private final TextButton[] homeButtons;
  private final GamePanel gamePanel;
  private final CustomModalBorder settingPanel;
  private final Option option;

  @Override
  public void previous(GameContext gameContext) {
    gameContext.changeState(GameStateType.WELCOME);
  }

  @Override
  public void next(GameContext gameContext) {
    gameContext.changeState(GameStateType.START);
  }

  @Override
  public void print() {}

  @Override
  public void render(Graphics g) {
    g.drawImage(background, 0, 0, GameConstant.GAME_WIDTH, GameConstant.GAME_HEIGHT, null);
    if (isId()) {
      for (var homeButton : homeButtons) {
        homeButton.render(g);
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (isId()) {
      for (var homeButton : homeButtons) {
        if (homeButton.isMouseOver(e)) {
          homeButton.setMousePressed(true);
          break;
        }
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (!isId()) {
      return;
    }
    for (int i = 0; i < homeButtons.length; ++i) {
      var homeButton = homeButtons[i];
      if (homeButton.isMouseOver(e)) {
        // 0: Start game
        // 1: Setting
        // 2: Quit
        switch (i) {
          case 0 -> GameContext.getInstance().next();
          case 1 ->
              ModalDialog.showModal(
                  gamePanel,
                  settingPanel,
                  option,
                  ApplicationConfiguration.getInstance().getHomeId());
          case 2 -> GameContext.getInstance().previousState();
        }
        break;
      }
    }
    reset();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (!isId()) {
      return;
    }
    for (var homeButton : homeButtons) {
      homeButton.setMouseOver(false);
    }
    for (var homeButton : homeButtons) {
      if (homeButton.isMouseOver(e)) {
        homeButton.setMouseOver(true);
        break;
      }
    }
  }

  private void reset() {
    for (var homeButton : homeButtons) {
      homeButton.reset();
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    GameState.super.mouseDragged(e);
  }

  private boolean isId() {
    return !ModalDialog.isIdExist(ApplicationConfiguration.getInstance().getHomeId());
  }
}
