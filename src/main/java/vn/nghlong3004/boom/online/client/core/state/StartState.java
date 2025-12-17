package vn.nghlong3004.boom.online.client.core.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import lombok.Builder;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.core.GamePanel;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
@Builder
public class StartState implements GameState {

  private final GamePanel gamePanel;

  private final BufferedImage background;
  private boolean installed;

  @Override
  public void next(GameContext gameContext) {}

  @Override
  public void mousePressed(MouseEvent e) {
    GameState.super.mousePressed(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    GameState.super.mouseReleased(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    GameState.super.mouseMoved(e);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    GameState.super.keyPressed(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    GameState.super.keyReleased(e);
  }

  @Override
  public void update() {
    if (!installed) {
      installed = true;
      SwingUtilities.invokeLater(this::install);
    }
  }

  @Override
  public void render(Graphics g) {
    if (background != null) {
      g.drawImage(background, 0, 0, GameConstant.GAME_WIDTH, GameConstant.GAME_HEIGHT, null);
    }
  }

  @Override
  public void previous(GameContext gameContext) {
    SwingUtilities.invokeLater(this::uninstall);
    gameContext.changeState(GameStateType.HOME);
  }

  private void install() {}

  private void uninstall() {}
}
