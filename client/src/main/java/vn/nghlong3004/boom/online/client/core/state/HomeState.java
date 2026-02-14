package vn.nghlong3004.boom.online.client.core.state;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import lombok.Builder;
import raven.modal.ModalDialog;
import raven.modal.option.Option;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.controller.view.CustomModalBorder;
import vn.nghlong3004.boom.online.client.controller.view.component.TextButton;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.core.GamePanel;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.UserSession;

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
  public void render(Graphics g) {
    g.drawImage(background, 0, 0, GameConstant.GAME_WIDTH, GameConstant.GAME_HEIGHT, null);
    if (isModalOpen()) {
      for (var homeButton : homeButtons) {
        homeButton.render(g);
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (isModalOpen()) {
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
    if (!isModalOpen()) {
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
                  gamePanel, settingPanel, option, ApplicationSession.getInstance().getHomeId());
          case 2 -> {
            UserSession.getInstance().clear();
            GameContext.getInstance().previous();
          }
        }
        break;
      }
    }
    reset();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (!isModalOpen()) {
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

  private boolean isModalOpen() {
    return !ModalDialog.isIdExist(ApplicationSession.getInstance().getHomeId());
  }
}
