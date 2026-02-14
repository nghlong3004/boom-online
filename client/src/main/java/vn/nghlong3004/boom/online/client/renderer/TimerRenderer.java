package vn.nghlong3004.boom.online.client.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import vn.nghlong3004.boom.online.client.constant.PlayingConstant;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class TimerRenderer {

  private static final Color TIMER_BG = new Color(0, 0, 0, 180);
  private static final Color TIMER_COLOR = new Color(255, 255, 255);
  private static final Color TIMER_WARNING_COLOR = new Color(255, 80, 80);

  private static final Font TIMER_FONT = new Font("SansSerif", Font.BOLD, 28);

  private static final int TIMER_WIDTH = 120;
  private static final int TIMER_HEIGHT = 45;
  private static final int TIMER_Y = 10;

  public void render(Graphics2D g2d, String timeText, int remainingSeconds) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int timerX = (PlayingConstant.MAP_WIDTH - TIMER_WIDTH) / 2;

    g2d.setColor(TIMER_BG);
    g2d.fill(new RoundRectangle2D.Float(timerX, TIMER_Y, TIMER_WIDTH, TIMER_HEIGHT, 15, 15));

    g2d.setFont(TIMER_FONT);
    g2d.setColor(remainingSeconds <= 30 ? TIMER_WARNING_COLOR : TIMER_COLOR);
    int textWidth = g2d.getFontMetrics().stringWidth(timeText);
    g2d.drawString(timeText, timerX + (TIMER_WIDTH - textWidth) / 2, TIMER_Y + 32);
  }
}
