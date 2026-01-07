package vn.nghlong3004.boom.online.client.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import vn.nghlong3004.boom.online.client.constant.AnimationConstant;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.constant.PlayingConstant;
import vn.nghlong3004.boom.online.client.model.playing.PlayerInfo;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/29/2025
 */
public class HudRenderer {

  private static final String[] ICONS =
      new String[] {"\uD83D\uDE12", "\uD83D\uDE0E", "\uD83D\uDE08", "\uD83D\uDE0F"};

  private static final int EMOTION_OFFSET_X = 10;
  private static final int EMOTION_OFFSET_Y = 14;

  private static final Color BACKGROUND_START = new Color(25, 25, 35, 240);
  private static final Color BACKGROUND_END = new Color(40, 40, 55, 240);
  private static final Color CARD_BACKGROUND = new Color(50, 50, 65, 200);
  private static final Color CARD_HIGHLIGHT = new Color(70, 70, 90, 200);
  private static final Color TEXT_COLOR = new Color(245, 245, 250);
  private static final Color TEXT_SECONDARY = new Color(180, 180, 190);
  private static final Color HOST_BADGE_COLOR = new Color(255, 200, 50);
  private static final Color HEALTH_BG = new Color(60, 60, 70);
  private static final Color HEALTH_COLOR = new Color(76, 217, 100);
  private static final Color DEAD_OVERLAY = new Color(0, 0, 0, 150);

  private static final Font NAME_FONT = new Font("SansSerif", Font.BOLD, 13);
  private static final Font SLOT_FONT = new Font("SansSerif", Font.BOLD, 11);
  private static final Font EMOTION_FONT = new Font("SansSerif", Font.PLAIN, 20);

  public void render(Graphics g, List<PlayerInfo> players) {
    Graphics2D g2d = (Graphics2D) g;
    enableAntiAliasing(g2d);
    renderBackground(g2d);
    renderPlayerCards(g2d, players);
  }

  private void enableAntiAliasing(Graphics2D g2d) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  }

  private void renderBackground(Graphics2D g2d) {
    GradientPaint gradient =
        new GradientPaint(
            PlayingConstant.HUD_X,
            0,
            BACKGROUND_START,
            PlayingConstant.HUD_X + PlayingConstant.HUD_WIDTH,
            0,
            BACKGROUND_END);
    g2d.setPaint(gradient);
    g2d.fillRect(PlayingConstant.HUD_X, 0, PlayingConstant.HUD_WIDTH, GameConstant.GAME_HEIGHT);
  }

  private void renderPlayerCards(Graphics2D g2d, List<PlayerInfo> players) {
    if (players == null) {
      return;
    }

    for (int i = 0; i < PlayingConstant.MAX_PLAYERS; i++) {
      PlayerInfo player = (i < players.size()) ? players.get(i) : null;
      renderPlayerCard(g2d, player, i);
    }
  }

  private void renderPlayerCard(Graphics2D g2d, PlayerInfo player, int index) {
    int margin = PlayingConstant.PLAYER_CARD_MARGIN;
    int cardX = PlayingConstant.HUD_X + margin;
    int cardY = index * PlayingConstant.PLAYER_CARD_HEIGHT + margin;
    int cardWidth = PlayingConstant.HUD_WIDTH - margin * 2;
    int cardHeight = PlayingConstant.PLAYER_CARD_HEIGHT - margin * 2;

    Color playerColor = PlayingConstant.PLAYER_COLORS[index % PlayingConstant.PLAYER_COLORS.length];

    renderCardBackground(g2d, cardX, cardY, cardWidth, cardHeight, playerColor);

    if (player != null) {
      renderPlayerContent(g2d, player, cardX, cardY, cardWidth, cardHeight);
    } else {
      renderEmptySlot(g2d, index, cardX, cardY, cardWidth, cardHeight);
    }
  }

  private void renderCardBackground(
      Graphics2D g2d, int x, int y, int width, int height, Color accentColor) {
    RoundRectangle2D card =
        new RoundRectangle2D.Float(
            x, y, width, height, PlayingConstant.PLAYER_CARD_ARC, PlayingConstant.PLAYER_CARD_ARC);

    g2d.setColor(CARD_BACKGROUND);
    g2d.fill(card);

    g2d.setColor(accentColor);
    g2d.setStroke(new BasicStroke(2));
    g2d.draw(card);
  }

  private void renderPlayerContent(
      Graphics2D g2d, PlayerInfo player, int cardX, int cardY, int cardWidth, int cardHeight) {

    int padding = PlayingConstant.PLAYER_CARD_PADDING;
    int avatarSize = PlayingConstant.AVATAR_SIZE;

    int avatarX = cardX + (cardWidth - avatarSize) / 2;
    int avatarY = cardY + padding;

    renderAvatar(g2d, player, avatarX, avatarY, avatarSize);

    int textY = avatarY + avatarSize + padding + 8;
    renderPlayerName(g2d, player, cardX, textY, cardWidth);

    int healthY = textY + 10;
    renderHealthBar(g2d, player, cardX + padding, healthY, cardWidth - padding * 2);

    renderEmotion(g2d, player, avatarX + avatarSize - 12, avatarY - 4);

    if (!player.isAlive()) {
      renderDeadOverlay(g2d, cardX, cardY, cardWidth, cardHeight);
    }
  }

  private void renderAvatar(Graphics2D g2d, PlayerInfo player, int x, int y, int size) {

    BufferedImage avatar = player.getAvatar();
    if (avatar != null) {
      g2d.setClip(x, y, size, size);
      g2d.drawImage(avatar, x, y, size, size, null);
      g2d.setClip(null);
    } else {
      g2d.setColor(CARD_HIGHLIGHT);
      g2d.fillRect(x, y, size, size);
    }
  }

  private void renderPlayerName(
      Graphics2D g2d, PlayerInfo player, int cardX, int y, int cardWidth) {
    g2d.setFont(NAME_FONT);
    g2d.setColor(TEXT_COLOR);

    String name = truncateName(player.getDisplayName(), 12);
    int textWidth = g2d.getFontMetrics().stringWidth(name);
    int textX = cardX + (cardWidth - textWidth) / 2;
    if (player.getUserId().equals(UserSession.getInstance().getCurrentUser().getId())) {
      g2d.setColor(HOST_BADGE_COLOR);
    }
    g2d.drawString(name, textX, y);
  }

  private void renderHealthBar(Graphics2D g2d, PlayerInfo player, int x, int y, int width) {
    int barWidth = Math.min(width, PlayingConstant.HEALTH_BAR_WIDTH);
    int barX = x + (width - barWidth) / 2;
    int barHeight = PlayingConstant.HEALTH_BAR_HEIGHT;

    g2d.setColor(HEALTH_BG);
    g2d.fillRoundRect(barX, y, barWidth, barHeight, 4, 4);

    if (player.isAlive() && player.getLives() > 0) {
      int maxLives = 1;
      int filledWidth = (int) ((player.getLives() / (float) maxLives) * barWidth);
      g2d.setColor(HEALTH_COLOR);
      g2d.fillRoundRect(barX, y, filledWidth, barHeight, 4, 4);
    }

    g2d.setColor(TEXT_SECONDARY);
    g2d.setFont(SLOT_FONT);
    String livesText = "♥ " + player.getLives();
    int textWidth = g2d.getFontMetrics().stringWidth(livesText);
    g2d.drawString(livesText, barX + (barWidth - textWidth) / 2, y + barHeight + 14);
  }

  private void renderEmotion(Graphics2D g2d, PlayerInfo player, int x, int y) {
    long currentTime = System.currentTimeMillis();

    int individualizedIndex =
        (int)
            (((currentTime + player.getSlotIndex() * AnimationConstant.EMOTION_DURATION_MS)
                    / AnimationConstant.EMOTION_DURATION_MS)
                % ICONS.length);

    g2d.setFont(EMOTION_FONT);
    g2d.setColor(Color.WHITE);
    g2d.drawString(ICONS[individualizedIndex], x + EMOTION_OFFSET_X, y + EMOTION_OFFSET_Y);
  }

  private void renderEmptySlot(
      Graphics2D g2d, int index, int cardX, int cardY, int cardWidth, int cardHeight) {
    g2d.setColor(TEXT_SECONDARY);
    g2d.setFont(SLOT_FONT);
    String text = "Slot " + (index + 1);
    int textWidth = g2d.getFontMetrics().stringWidth(text);
    int textX = cardX + (cardWidth - textWidth) / 2;
    int textY = cardY + cardHeight / 2 + 4;
    g2d.drawString(text, textX, textY);
  }

  private void renderDeadOverlay(Graphics2D g2d, int x, int y, int width, int height) {
    RoundRectangle2D overlay =
        new RoundRectangle2D.Float(
            x, y, width, height, PlayingConstant.PLAYER_CARD_ARC, PlayingConstant.PLAYER_CARD_ARC);
    g2d.setColor(DEAD_OVERLAY);
    g2d.fill(overlay);

    g2d.setColor(new Color(255, 80, 80));
    g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
    String deadText = "DEAD";
    int textWidth = g2d.getFontMetrics().stringWidth(deadText);
    g2d.drawString(deadText, x + (width - textWidth) / 2, y + height - 10);
  }

  private String truncateName(String name, int maxLength) {
    if (name == null) {
      return "Player";
    }
    if (name.length() <= maxLength) {
      return name;
    }
    return name.substring(0, maxLength - 1) + "…";
  }
}
