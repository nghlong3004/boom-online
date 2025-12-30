package vn.nghlong3004.boom.online.client.core;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.core.state.GameState;
import vn.nghlong3004.boom.online.client.core.state.GameStateType;
import vn.nghlong3004.boom.online.client.input.KeyboardAdapter;
import vn.nghlong3004.boom.online.client.input.MouseAdapter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Slf4j
public class GameContext implements GameLogic, MouseAdapter, KeyboardAdapter {

  @Getter private GameState state;
  @Setter private Map<GameStateType, GameState> stateMap;

  private GameContext() {}

  public static GameContext getInstance() {
    return HOLDER.INSTANCE;
  }

  public void previous() {
    state.previous(this);
  }

  public void next() {
    state.next(this);
  }

  public void changeState(GameStateType type) {
    if (stateMap != null) {
      if (stateMap.containsKey(type)) {
        state = stateMap.get(type);
      }
    }
  }

  @Override
  public void update() {
    if (state != null) {
      state.update();
    }
  }

  @Override
  public void render(Graphics g) {
    if (state != null) {
      state.render(g);
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (state != null) {
      state.keyPressed(e);
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (state != null) {
      state.keyReleased(e);
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (state != null) {
      state.mouseClicked(e);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (state != null) {
      state.mousePressed(e);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (state != null) {
      state.mouseReleased(e);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (state != null) {
      state.mouseDragged(e);
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (state != null) {
      state.mouseMoved(e);
    }
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (state != null) {
      state.mouseWheelMoved(e);
    }
  }

  private static final class HOLDER {
    private static final GameContext INSTANCE = new GameContext();
  }
}
