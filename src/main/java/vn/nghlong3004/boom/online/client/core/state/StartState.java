package vn.nghlong3004.boom.online.client.core.state;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import lombok.Builder;
import raven.modal.ModalDialog;
import raven.modal.option.Option;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.controller.view.CustomModalBorder;
import vn.nghlong3004.boom.online.client.controller.view.lobby.LobbyPanel;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.core.GameObjectContainer;
import vn.nghlong3004.boom.online.client.core.GamePanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.UserSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;

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
  private final Option option;
  private boolean installed;

  @Override
  public void next(GameContext gameContext) {
    gameContext.changeState(GameStateType.START);
  }

  @Override
  public void previous(GameContext gameContext) {
    SwingUtilities.invokeLater(this::uninstall);
    gameContext.changeState(GameStateType.HOME);
  }

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

  private void install() {
    String startId = ApplicationSession.getInstance().getStartId();
    if (ModalDialog.isIdExist(startId)) {
      return;
    }

    if (ApplicationSession.getInstance().isOfflineMode()) {
      handleOffline(startId);
    } else {
      handleOnline(startId);
    }
  }

  private void uninstall() {
    String startId = ApplicationSession.getInstance().getStartId();
    if (ModalDialog.isIdExist(startId)) {
      ModalDialog.closeModal(startId);
    }
    installed = false;
  }

  private void handleOnline(String startId) {
    RoomService roomService = GameObjectContainer.getOnlineRoomService();
    LobbyPanel lobbyPanel = new LobbyPanel(roomService, startId);
    CustomModalBorder lobbyBorder =
        new CustomModalBorder(lobbyPanel, I18NUtil.getString("lobby.title"), null);

    ModalDialog.showModal(gamePanel, lobbyBorder, option, startId);
  }

  private void handleOffline(String startId) {
    RoomService roomService = GameObjectContainer.getOfflineRoomService();
    User me = UserSession.getInstance().getCurrentUser();
    if (me == null) {
      GameContext.getInstance().previous();
      return;
    }
  }
}
