package vn.nghlong3004.boom.online.client.core.state;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
import static vn.nghlong3004.boom.online.client.constant.GameConstant.GAME_HEIGHT;
import static vn.nghlong3004.boom.online.client.constant.GameConstant.GAME_WIDTH;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import raven.modal.ModalDialog;
import raven.modal.option.Option;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.core.GamePanel;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.view.CustomModalBorder;
import vn.nghlong3004.boom.online.client.view.component.TextButton;

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
  private final TextButton textButton;
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
    if (isModalOpen()) {
      textButton.render(g);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (isModalOpen()) {
      if (textButton.isMouseOver(e)) {
        textButton.setMousePressed(true);
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (isModalOpen()) {
      if (textButton.isMouseOver(e)) {
        ModalDialog.showModal(
            gamePanel, loginPanel, option, ApplicationSession.getInstance().getWelcomeId());
      }
      textButton.reset();
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (isModalOpen()) {
      textButton.setMouseOver(textButton.isMouseOver(e));
    }
  }

  private boolean isModalOpen() {
    return !ModalDialog.isIdExist(ApplicationSession.getInstance().getWelcomeId());
  }
}
