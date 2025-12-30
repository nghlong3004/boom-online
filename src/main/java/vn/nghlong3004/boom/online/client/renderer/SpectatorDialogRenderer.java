package vn.nghlong3004.boom.online.client.renderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class SpectatorDialogRenderer {

  private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 150);
  private static final Color PANEL_COLOR = new Color(40, 40, 40, 240);
  private static final Color BORDER_COLOR = new Color(255, 200, 0);
  private static final Color TEXT_COLOR = Color.WHITE;
  private static final Color HIGHLIGHT_COLOR = new Color(255, 200, 0);
  private static final Color BUTTON_COLOR = new Color(60, 60, 60);
  private static final Color BUTTON_HOVER_COLOR = new Color(80, 80, 80);
  private static final Color BUTTON_TEXT_COLOR = Color.WHITE;

  private static final int PANEL_WIDTH = 400;
  private static final int PANEL_HEIGHT = 200;
  private static final int BUTTON_WIDTH = 150;
  private static final int BUTTON_HEIGHT = 40;
  private static final int BUTTON_GAP = 20;

  private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
  private static final Font MESSAGE_FONT = new Font("Arial", Font.PLAIN, 16);
  private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

  private float alpha;
  @Getter
  private int selectedButton; // 0 = spectate, 1 = exit
  @Getter
  private boolean visible;

  private Rectangle spectateButtonBounds;
  private Rectangle exitButtonBounds;

  public SpectatorDialogRenderer() {
    this.alpha = 0f;
    this.selectedButton = 0;
    this.visible = false;
  }

  public void show() {
    this.visible = true;
    this.alpha = 0f;
    this.selectedButton = 0;
  }

  public void hide() {
    this.visible = false;
    this.alpha = 0f;
  }

  public void update() {
    if (visible && alpha < 1f) {
      alpha = Math.min(1f, alpha + 0.05f);
    }
  }

  public void selectLeft() {
    selectedButton = 0;
  }

  public void selectRight() {
    selectedButton = 1;
  }

  public void render(Graphics2D g2d, int screenWidth, int screenHeight) {
    if (!visible) {
      return;
    }

    Graphics2D g = (Graphics2D) g2d.create();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

    g.setColor(OVERLAY_COLOR);
    g.fillRect(0, 0, screenWidth, screenHeight);

    int panelX = (screenWidth - PANEL_WIDTH) / 2;
    int panelY = (screenHeight - PANEL_HEIGHT) / 2;

    RoundRectangle2D panel =
        new RoundRectangle2D.Float(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 20, 20);
    g.setColor(PANEL_COLOR);
    g.fill(panel);

    g.setColor(BORDER_COLOR);
    g.setStroke(new java.awt.BasicStroke(3));
    g.draw(panel);

    g.setFont(TITLE_FONT);
    g.setColor(HIGHLIGHT_COLOR);
    String title = "BẠN ĐÃ BỊ LOẠI!";
    FontMetrics titleFm = g.getFontMetrics();
    int titleX = panelX + (PANEL_WIDTH - titleFm.stringWidth(title)) / 2;
    int titleY = panelY + 50;
    g.drawString(title, titleX, titleY);

    g.setFont(MESSAGE_FONT);
    g.setColor(TEXT_COLOR);
    String message = "Bạn muốn xem tiếp trận đấu hay quay về sảnh?";
    FontMetrics msgFm = g.getFontMetrics();
    int msgX = panelX + (PANEL_WIDTH - msgFm.stringWidth(message)) / 2;
    int msgY = panelY + 90;
    g.drawString(message, msgX, msgY);

    int buttonsY = panelY + PANEL_HEIGHT - BUTTON_HEIGHT - 30;
    int totalButtonsWidth = BUTTON_WIDTH * 2 + BUTTON_GAP;
    int buttonsStartX = panelX + (PANEL_WIDTH - totalButtonsWidth) / 2;

    updateButtonBounds(buttonsStartX, buttonsY, screenWidth, screenHeight);

    drawButton(g, "XEM TIẾP", buttonsStartX, buttonsY, selectedButton == 0);

    drawButton(
        g, "VỀ SẢNH", buttonsStartX + BUTTON_WIDTH + BUTTON_GAP, buttonsY, selectedButton == 1);

    g.dispose();
  }

  private void updateButtonBounds(
      int buttonsStartX, int buttonsY, int screenWidth, int screenHeight) {
    this.spectateButtonBounds = new Rectangle(buttonsStartX, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT);
    this.exitButtonBounds =
        new Rectangle(
            buttonsStartX + BUTTON_WIDTH + BUTTON_GAP, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT);
  }

  public int handleMouseClick(int mouseX, int mouseY) {
    if (!visible || spectateButtonBounds == null || exitButtonBounds == null) {
      return -1;
    }

    if (spectateButtonBounds.contains(mouseX, mouseY)) {
      return 0;
    }
    if (exitButtonBounds.contains(mouseX, mouseY)) {
      return 1;
    }
    return -1;
  }

  public void handleMouseMove(int mouseX, int mouseY) {
    if (!visible || spectateButtonBounds == null || exitButtonBounds == null) {
      return;
    }

    if (spectateButtonBounds.contains(mouseX, mouseY)) {
      selectedButton = 0;
    } else if (exitButtonBounds.contains(mouseX, mouseY)) {
      selectedButton = 1;
    }
  }

  private void drawButton(Graphics2D g, String text, int x, int y, boolean selected) {
    RoundRectangle2D button = new RoundRectangle2D.Float(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, 10, 10);

    g.setColor(selected ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
    g.fill(button);

    g.setColor(selected ? HIGHLIGHT_COLOR : new Color(100, 100, 100));
    g.setStroke(new java.awt.BasicStroke(selected ? 2 : 1));
    g.draw(button);

    g.setFont(BUTTON_FONT);
    g.setColor(selected ? HIGHLIGHT_COLOR : BUTTON_TEXT_COLOR);
    FontMetrics fm = g.getFontMetrics();
    int textX = x + (BUTTON_WIDTH - fm.stringWidth(text)) / 2;
    int textY = y + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent()) / 2;
    g.drawString(text, textX, textY);
  }
}
