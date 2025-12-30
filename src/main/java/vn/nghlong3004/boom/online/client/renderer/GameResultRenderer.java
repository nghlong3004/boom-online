package vn.nghlong3004.boom.online.client.renderer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import vn.nghlong3004.boom.online.client.constant.PlayingConstant;
import vn.nghlong3004.boom.online.client.model.game.GameResult;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class GameResultRenderer {

  private static final Color WIN_COLOR_START = new Color(46, 204, 113);
  private static final Color WIN_COLOR_END = new Color(39, 174, 96);
  private static final Color LOSE_COLOR_START = new Color(231, 76, 60);
  private static final Color LOSE_COLOR_END = new Color(192, 57, 43);
  private static final Color DRAW_COLOR_START = new Color(241, 196, 15);
  private static final Color DRAW_COLOR_END = new Color(243, 156, 18);
  private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 180);
  private static final Color TEXT_COLOR = Color.WHITE;
  private static final Color SHADOW_COLOR = new Color(0, 0, 0, 100);
  private static final Color BUTTON_COLOR = new Color(52, 73, 94);
  private static final Color BUTTON_HOVER_COLOR = new Color(44, 62, 80);
  private static final Color BUTTON_BORDER_COLOR = new Color(255, 255, 255, 100);

  private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 64);
  private static final Font MESSAGE_FONT = new Font("SansSerif", Font.PLAIN, 24);
  private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 18);

  private static final int PANEL_WIDTH = 500;
  private static final int PANEL_HEIGHT = 280;
  private static final int PANEL_ARC = 30;
  private static final int BUTTON_WIDTH = 180;
  private static final int BUTTON_HEIGHT = 50;

  private float animationProgress = 0f;
  private static final float ANIMATION_SPEED = 0.05f;

  private Rectangle buttonBounds;
  private boolean buttonHovered = false;

  public void update() {
    if (animationProgress < 1f) {
      animationProgress += ANIMATION_SPEED;
      if (animationProgress > 1f) {
        animationProgress = 1f;
      }
    }
  }

  public void reset() {
    animationProgress = 0f;
    buttonBounds = null;
    buttonHovered = false;
  }

  public boolean isAnimationComplete() {
    return animationProgress >= 1f;
  }

  public boolean handleMouseClick(int mouseX, int mouseY) {
    return buttonBounds != null && buttonBounds.contains(mouseX, mouseY);
  }

  public void handleMouseMove(int mouseX, int mouseY) {
    if (buttonBounds != null) {
      buttonHovered = buttonBounds.contains(mouseX, mouseY);
    }
  }

  public void render(Graphics2D g2d, GameResult result) {
    if (result == null) {
      return;
    }

    enableAntiAliasing(g2d);

    int mapWidth = PlayingConstant.MAP_WIDTH;
    int mapHeight = PlayingConstant.MAP_HEIGHT;

    renderOverlay(g2d, mapWidth, mapHeight);

    int panelX = (mapWidth - PANEL_WIDTH) / 2;
    int panelY = (mapHeight - PANEL_HEIGHT) / 2;

    float scale = easeOutBack(animationProgress);
    int scaledWidth = (int) (PANEL_WIDTH * scale);
    int scaledHeight = (int) (PANEL_HEIGHT * scale);
    int scaledX = panelX + (PANEL_WIDTH - scaledWidth) / 2;
    int scaledY = panelY + (PANEL_HEIGHT - scaledHeight) / 2;

    renderPanel(g2d, result, scaledX, scaledY, scaledWidth, scaledHeight);

    if (animationProgress >= 0.5f) {
      float textAlpha = Math.min(1f, (animationProgress - 0.5f) * 2);
      renderText(g2d, result, panelX, panelY, textAlpha);
    }
  }

  private void enableAntiAliasing(Graphics2D g2d) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  }

  private void renderOverlay(Graphics2D g2d, int width, int height) {
    float overlayAlpha = Math.min(1f, animationProgress * 2);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha * 0.7f));
    g2d.setColor(OVERLAY_COLOR);
    g2d.fillRect(0, 0, width, height);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
  }

  private void renderPanel(Graphics2D g2d, GameResult result, int x, int y, int width, int height) {
    if (width <= 0 || height <= 0) {
      return;
    }

    Color startColor;
    Color endColor;

    switch (result) {
      case WIN -> {
        startColor = WIN_COLOR_START;
        endColor = WIN_COLOR_END;
      }
      case LOSE -> {
        startColor = LOSE_COLOR_START;
        endColor = LOSE_COLOR_END;
      }
      default -> {
        startColor = DRAW_COLOR_START;
        endColor = DRAW_COLOR_END;
      }
    }

    g2d.setColor(SHADOW_COLOR);
    RoundRectangle2D shadow =
        new RoundRectangle2D.Float(x + 8, y + 8, width, height, PANEL_ARC, PANEL_ARC);
    g2d.fill(shadow);

    GradientPaint gradient = new GradientPaint(x, y, startColor, x, y + height, endColor);
    g2d.setPaint(gradient);
    RoundRectangle2D panel = new RoundRectangle2D.Float(x, y, width, height, PANEL_ARC, PANEL_ARC);
    g2d.fill(panel);

    g2d.setColor(new Color(255, 255, 255, 50));
    g2d.setStroke(new java.awt.BasicStroke(3));
    g2d.draw(panel);
  }

  private void renderText(Graphics2D g2d, GameResult result, int panelX, int panelY, float alpha) {
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

    g2d.setFont(TITLE_FONT);
    String title = result.getTitle();
    int titleWidth = g2d.getFontMetrics().stringWidth(title);
    int titleX = panelX + (PANEL_WIDTH - titleWidth) / 2;
    int titleY = panelY + 90;

    g2d.setColor(SHADOW_COLOR);
    g2d.drawString(title, titleX + 3, titleY + 3);
    g2d.setColor(TEXT_COLOR);
    g2d.drawString(title, titleX, titleY);

    g2d.setFont(MESSAGE_FONT);
    String message = result.getMessage();
    int messageWidth = g2d.getFontMetrics().stringWidth(message);
    int messageX = panelX + (PANEL_WIDTH - messageWidth) / 2;
    int messageY = panelY + 150;

    g2d.setColor(new Color(255, 255, 255, 200));
    g2d.drawString(message, messageX, messageY);
    int buttonX = panelX + (PANEL_WIDTH - BUTTON_WIDTH) / 2;
    int buttonY = panelY + PANEL_HEIGHT - BUTTON_HEIGHT - 30;
    buttonBounds = new Rectangle(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

    renderButton(g2d, "VỀ SẢNH", buttonX, buttonY, buttonHovered);

    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
  }

  private void renderButton(Graphics2D g2d, String text, int x, int y, boolean hovered) {
    RoundRectangle2D button = new RoundRectangle2D.Float(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, 15, 15);
    g2d.setColor(SHADOW_COLOR);
    RoundRectangle2D shadow =
        new RoundRectangle2D.Float(x + 3, y + 3, BUTTON_WIDTH, BUTTON_HEIGHT, 15, 15);
    g2d.fill(shadow);

    g2d.setColor(hovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
    g2d.fill(button);

    g2d.setColor(hovered ? TEXT_COLOR : BUTTON_BORDER_COLOR);
    g2d.setStroke(new BasicStroke(hovered ? 3 : 2));
    g2d.draw(button);

    g2d.setFont(BUTTON_FONT);
    g2d.setColor(TEXT_COLOR);
    FontMetrics fm = g2d.getFontMetrics();
    int textX = x + (BUTTON_WIDTH - fm.stringWidth(text)) / 2;
    int textY = y + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent()) / 2;
    g2d.drawString(text, textX, textY);
  }

  private float easeOutBack(float t) {
    float c1 = 1.70158f;
    float c3 = c1 + 1;
    return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
  }
}
