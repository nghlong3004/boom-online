package vn.nghlong3004.boom.online.client.view.component;

import java.awt.*;
import java.awt.event.MouseEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public abstract class ButtonAdapter {
  @Getter @Setter protected boolean mousePressed, mouseOver;
  @Getter protected int x, y, width, height;
  protected int rowIndex, columnIndex;
  protected Rectangle box;

  private boolean wasMouseOver = false;

  protected ButtonAdapter(int x, int y, int width, int height, int rowIndex) {
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;
    this.box = new Rectangle(x, y, width, height);
    this.rowIndex = rowIndex;
  }

  protected ButtonAdapter(int x, int y, int width, int height) {
    this(x, y, width, height, 0);
  }

  protected abstract void loadImage();

  protected abstract void render(Graphics g);

  public void reset() {
    setMouseOver(false);
    setMousePressed(false);
    wasMouseOver = false;
  }

  public void setMouseOver(boolean mouseOver) {
    if (!wasMouseOver && mouseOver) {
      playHoverSound();
    }
    this.wasMouseOver = this.mouseOver;
    this.mouseOver = mouseOver;
  }

  protected void playHoverSound() {}

  public boolean isMouseOver(MouseEvent e) {
    return box.contains(e.getX(), e.getY());
  }
}
