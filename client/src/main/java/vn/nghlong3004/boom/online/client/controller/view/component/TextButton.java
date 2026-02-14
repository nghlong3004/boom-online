package vn.nghlong3004.boom.online.client.controller.view.component;

import static vn.nghlong3004.boom.online.client.constant.ButtonConstant.BUTTON_HEIGHT;
import static vn.nghlong3004.boom.online.client.constant.ButtonConstant.BUTTON_WIDTH;
import static vn.nghlong3004.boom.online.client.constant.GameConstant.SCALE;
import static vn.nghlong3004.boom.online.client.constant.ImageConstant.BUTTON;
import static vn.nghlong3004.boom.online.client.constant.ImageConstant.BUTTON_TOUCH;

import java.awt.*;
import java.awt.image.BufferedImage;
import vn.nghlong3004.boom.online.client.util.ImageUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public class TextButton extends ButtonAdapter {
  private BufferedImage buttonImage;
  private BufferedImage buttonTouchImage;
  private final String text;

  public TextButton(int x, int y, String text) {
    super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, 0);
    this.text = text;
    loadImage();
  }

  @Override
  protected void loadImage() {
    buttonImage = ImageUtil.loadImage(BUTTON);
    buttonTouchImage = ImageUtil.loadImage(BUTTON_TOUCH);
  }

  @Override
  public void render(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    BufferedImage currentImage = (mouseOver || mousePressed) ? buttonTouchImage : buttonImage;

    if (currentImage != null) {
      g.drawImage(currentImage, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
    }

    int fontSize = (int) (24 * SCALE);
    Font font = new Font("Arial", Font.BOLD, fontSize);
    g2d.setFont(font);

    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(text);
    int textHeight = fm.getHeight();

    int textX = x + (BUTTON_WIDTH - textWidth) / 2;
    int textY = y + (BUTTON_HEIGHT - textHeight) / 2 + fm.getAscent();

    if (mousePressed) {
      textY += 2;
    }

    g2d.setColor(new Color(0, 0, 0, 100));
    g2d.drawString(text, textX + 2, textY + 2);

    if (mouseOver || mousePressed) {
      g2d.setColor(new Color(255, 255, 100));
    } else {
      g2d.setColor(Color.WHITE);
    }
    g2d.drawString(text, textX, textY);
  }
}
