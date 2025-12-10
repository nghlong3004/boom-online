package com.vn.nghlong3004.client.context;

import com.vn.nghlong3004.client.game.GameAdapter;
import com.vn.nghlong3004.client.game.state.GameState;
import com.vn.nghlong3004.client.game.state.GameStateType;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@Slf4j
@RequiredArgsConstructor
public class GameContext implements GameAdapter {

  private GameState state;
  @Setter private Map<GameStateType, GameState> stateMap;

  public void previousState() {
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
}
