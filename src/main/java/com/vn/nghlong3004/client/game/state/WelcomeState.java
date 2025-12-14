package com.vn.nghlong3004.client.game.state;

import static com.vn.nghlong3004.client.constant.GameConstant.*;

import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.context.GameContext;
import com.vn.nghlong3004.client.controller.view.CustomModalBorder;
import com.vn.nghlong3004.client.controller.view.welcome.ButtonAdapter;
import com.vn.nghlong3004.client.game.GamePanel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import raven.modal.ModalDialog;
import raven.modal.option.Option;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@Slf4j
@Builder
public class WelcomeState implements GameState {
  private final GamePanel gamePanel;
  private final CustomModalBorder loginPanel;
  private final Option option;
  private final ButtonAdapter buttonAdapter;
  private final BufferedImage background;

  @Override
  public void previous(GameContext gameContext) {
    gameContext.changeState(GameStateType.WELCOME);
  }

  @Override
  public void next(GameContext gameContext) {
    gameContext.changeState(GameStateType.HOME);
  }

  @Override
  public void render(Graphics g) {
    g.drawImage(background, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
    if (!ModalDialog.isIdExist(ApplicationConfiguration.getInstance().getWelcomeId())) {
      buttonAdapter.render(g);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (!ModalDialog.isIdExist(ApplicationConfiguration.getInstance().getWelcomeId())) {
      if (buttonAdapter.isMouseOver(e)) {
        buttonAdapter.setMousePressed(true);
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (!ModalDialog.isIdExist(ApplicationConfiguration.getInstance().getWelcomeId())) {
      if (buttonAdapter.isMouseOver(e)) {
        ModalDialog.showModal(
            gamePanel, loginPanel, option, ApplicationConfiguration.getInstance().getWelcomeId());
      }
      buttonAdapter.reset();
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (!ModalDialog.isIdExist(ApplicationConfiguration.getInstance().getWelcomeId())) {
      buttonAdapter.setMouseOver(false);
      if (buttonAdapter.isMouseOver(e)) {
        buttonAdapter.setMouseOver(true);
      }
    }
  }
}
