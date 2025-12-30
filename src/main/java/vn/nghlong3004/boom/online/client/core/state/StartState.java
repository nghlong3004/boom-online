package vn.nghlong3004.boom.online.client.core.state;

import com.google.gson.Gson;
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
import vn.nghlong3004.boom.online.client.controller.view.component.TextButton;
import vn.nghlong3004.boom.online.client.controller.view.lobby.LobbyPanel;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.core.GameObjectContainer;
import vn.nghlong3004.boom.online.client.core.GamePanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
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
  private final TextButton textButton;
  private final Option option;
  private boolean installed;
  private LobbyPanel lobbyPanel;
  private CustomModalBorder lobbyBorder;

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
    if (isModalOpen()) {
      if (textButton.isMouseOver(e)) {
        textButton.setMousePressed(true);
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (isModalOpen()) {
      if (textButton.isMouseOver(e) && lobbyBorder != null) {
        ModalDialog.showModal(
            gamePanel, lobbyBorder, option, ApplicationSession.getInstance().getStartId());
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
      if (isModalOpen()) {
        textButton.render(g);
      }
    }
  }

  public void ping() {
    lobbyPanel.getRoomPanel().getPresenter().onToggleReadyClicked();
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
    Gson gson = GameObjectContainer.getGson();
    WebSocketService webSocketService = GameObjectContainer.getWebSocketService();
    lobbyPanel = new LobbyPanel(roomService, startId, webSocketService, gson);
    lobbyBorder = new CustomModalBorder(lobbyPanel, I18NUtil.getString("lobby.title"), null);
    lobbyPanel.getPresenter().init();

    ModalDialog.showModal(gamePanel, lobbyBorder, option, startId);
  }

  private void handleOffline(String startId) {
    RoomService roomService = GameObjectContainer.getOfflineRoomService();
    User me = UserSession.getInstance().getCurrentUser();
    if (me == null) {
      GameContext.getInstance().previous();
      return;
    }
    lobbyPanel = new LobbyPanel(roomService, startId, null, null);
    lobbyBorder = new CustomModalBorder(lobbyPanel, I18NUtil.getString("lobby.title"), null);
    ModalDialog.showModal(gamePanel, lobbyBorder, option, startId);
  }

  private boolean isModalOpen() {
    return !ModalDialog.isIdExist(ApplicationSession.getInstance().getStartId());
  }
}
