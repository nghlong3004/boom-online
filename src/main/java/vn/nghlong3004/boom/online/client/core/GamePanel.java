package vn.nghlong3004.boom.online.client.core;

import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.input.KeyboardInput;
import vn.nghlong3004.boom.online.client.input.MouseInput;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@Getter
@Slf4j
@RequiredArgsConstructor
public class GamePanel extends JPanel {
  private final GameContext gameContext;

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    gameContext.render(g);
  }

  protected void update() {
    gameContext.update();
  }

  protected void setting() {
    setInput();
    setSize();
  }

  private void setInput() {
    MouseInput mouseInput = new MouseInput(this);
    addKeyListener(new KeyboardInput(this));
    addMouseListener(mouseInput);
    addMouseWheelListener(mouseInput);
    addMouseMotionListener(mouseInput);
  }

  private void setSize() {
    Dimension size = new Dimension(GameConstant.GAME_WIDTH, GameConstant.GAME_HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
  }
}
