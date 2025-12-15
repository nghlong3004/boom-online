package com.vn.nghlong3004.client.controller.view.start.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.vn.nghlong3004.client.application.character.CharacterAvatarGateway;
import com.vn.nghlong3004.client.constant.GameConstant;
import com.vn.nghlong3004.client.constant.ImageConstant;
import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.controller.RoomView;
import com.vn.nghlong3004.client.controller.presenter.RoomPresenterImpl;
import com.vn.nghlong3004.client.controller.view.component.StartButton;
import com.vn.nghlong3004.client.domain.character.CharacterId;
import com.vn.nghlong3004.client.infrastructure.character.ClasspathCharacterAvatarGateway;
import com.vn.nghlong3004.client.model.room.ChatMessage;
import com.vn.nghlong3004.client.model.room.ChatMessageType;
import com.vn.nghlong3004.client.model.room.PlayerSlot;
import com.vn.nghlong3004.client.model.room.RoomState;
import com.vn.nghlong3004.client.service.RoomService;
import com.vn.nghlong3004.client.service.impl.InMemoryRoomServiceImpl;
import com.vn.nghlong3004.client.util.ImageUtil;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.util.NotificationUtil;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.util.List;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.miginfocom.swing.MigLayout;
import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
public class RoomPanel extends JPanel implements RoomView {

  private static final CharacterId[] CHARACTERS = {
    CharacterId.BOZ, CharacterId.EVIE, CharacterId.IKE, CharacterId.PLUNK
  };
  private static final String[] MAPS = {"Town", "Desert", "Underwater", "Xmas"};

  private static final int SELECTOR_AVATAR_SIZE = 72;
  private static final int HEADER_HEIGHT = 32;

  private final Runnable backToList;

  private final RoomPresenterImpl presenter;

  private final CharacterAvatarGateway avatarGateway;

  private final PlayerSlotPanel[] slotPanels = new PlayerSlotPanel[4];

  private final JTextPane chatPane;
  private final JTextField chatInput;

  private final JLabel characterAvatar;
  private final JLabel characterValue;
  private final JLabel mapValue;

  private final JButton mapPrev;
  private final JButton mapNext;

  private final JButton actionButton;

  public RoomPanel(Runnable backToList) {
    this.backToList = backToList;

    RoomService roomService = InMemoryRoomServiceImpl.getInstance();
    presenter = new RoomPresenterImpl(this, roomService);
    avatarGateway = ClasspathCharacterAvatarGateway.getInstance();

    int rightFixedWidth =
        Math.max(260, (int) (com.vn.nghlong3004.client.constant.GameConstant.GAME_WIDTH * 0.25));

    setLayout(
        new MigLayout(
            "fill,insets 16", "[grow,fill][" + rightFixedWidth + "!,fill]", "[][grow,fill]"));
    putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JButton backButton = createBackButton();

    JPanel header = new JPanel(new MigLayout("fill,insets 0", "[left][grow]", "[]"));
    header.putClientProperty(FlatClientProperties.STYLE, "background:null;");
    header.add(backButton, "w 40!, h 32!");
    header.add(new JLabel(""), "growx");
    add(header, "span 2, growx, h 32!, gapbottom 8, wrap");

    int chatFixedHeight =
        Math.max(180, (int) ((GameConstant.GAME_HEIGHT - HEADER_HEIGHT - 16 * 2 - 8) * 0.40));

    // LEFT
    JPanel left =
        new JPanel(
            new MigLayout(
                "fill,insets 0", "[grow,fill]", "[grow,fill][" + chatFixedHeight + "!,fill]"));
    left.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JPanel slots =
        new JPanel(
            new MigLayout(
                "fill,wrap 2,insets 0", "[grow,fill][grow,fill]", "[grow,fill][grow,fill]"));
    slots.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    for (int i = 0; i < 4; i++) {
      slotPanels[i] = new PlayerSlotPanel(avatarGateway);
      slots.add(slotPanels[i], "grow, wmin 0");
    }

    left.add(slots, "grow,wrap");

    JPanel chat = new JPanel(new MigLayout("fill,insets 0", "[grow,fill]", "[grow,fill][]"));
    chat.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    chatPane =
        new JTextPane() {
          @Override
          public boolean getScrollableTracksViewportWidth() {
            return true;
          }

          @Override
          public boolean getScrollableTracksViewportHeight() {
            return false;
          }
        };
    chatPane.setEditable(false);

    JScrollPane scroll = new JScrollPane(chatPane);
    scroll.putClientProperty(FlatClientProperties.STYLE, "arc:12;");
    scroll.setMinimumSize(new Dimension(0, 0));
    scroll.getVerticalScrollBar().setUnitIncrement(16);

    JPanel chatInputPanel =
        new JPanel(new MigLayout("fill,insets 0", "[grow,fill][fill,110]", "[]"));
    chatInputPanel.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    chatInput = new JTextField();
    chatInput.putClientProperty(
        FlatClientProperties.PLACEHOLDER_TEXT, text("room.chat.placeholder"));

    JButton send = new StartButton(text("room.chat.send"), true);

    chatInputPanel.add(chatInput, "growx");
    chatInputPanel.add(send);

    chat.add(scroll, "grow,wrap");
    chat.add(chatInputPanel, "growx");

    left.add(chat, "grow");

    add(left, "grow");

    // RIGHT
    JPanel right =
        new JPanel(new MigLayout("fill,insets 0,wrap 1", "[grow,fill]", "[]16[][push][]"));
    right.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JPanel character =
        new JPanel(
            new MigLayout(
                "fill,insets 0,wrap 3,al center center",
                "[fill,36!][grow,center][fill,36!]",
                "[]10[]"));
    character.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JButton charPrev = createIconButton(ImageConstant.ARROW_LEFT);
    JButton charNext = createIconButton(ImageConstant.ARROW_RIGHT);
    charPrev.setPreferredSize(new Dimension(36, 32));
    charNext.setPreferredSize(new Dimension(36, 32));

    characterAvatar = new JLabel();
    characterAvatar.setHorizontalAlignment(JLabel.CENTER);
    characterAvatar.setOpaque(true);
    characterAvatar.putClientProperty(FlatClientProperties.STYLE, "arc:18;" + "borderWidth:0;");
    characterAvatar.setIcon(avatarGateway.getAvatar(CHARACTERS[0], SELECTOR_AVATAR_SIZE));
    characterAvatar.setBackground(resolveCharacterBackground(CHARACTERS[0]));

    characterValue = new JLabel(CHARACTERS[0].name(), JLabel.CENTER);
    characterValue.putClientProperty(FlatClientProperties.STYLE, "font:bold +1;");
    characterValue.setToolTipText(CHARACTERS[0].name());

    // Arrows flank avatar; name below
    character.add(charPrev, "align center");
    character.add(
        characterAvatar,
        "align center, h " + SELECTOR_AVATAR_SIZE + "!, w " + SELECTOR_AVATAR_SIZE + "!");
    character.add(charNext, "align center, wrap");
    character.add(characterValue, "span 3, align center");

    JPanel map =
        new JPanel(new MigLayout("fill,insets 0", "[fill,36!][grow,fill][fill,36!]", "[]"));
    map.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    mapPrev = createIconButton(ImageConstant.ARROW_LEFT);
    mapNext = createIconButton(ImageConstant.ARROW_RIGHT);
    mapPrev.setPreferredSize(new Dimension(36, 32));
    mapNext.setPreferredSize(new Dimension(36, 32));
    mapValue = new JLabel(MAPS[0], JLabel.CENTER);
    mapValue.putClientProperty(FlatClientProperties.STYLE, "font:bold +1;");
    mapValue.setToolTipText(MAPS[0]);

    map.add(mapPrev);
    map.add(mapValue, "growx");
    map.add(mapNext);

    actionButton = new StartButton(text("room.button.ready"), true);

    JPanel characterSection = createSectionPanel(text("room.character.title"), character);
    JPanel mapSection = createSectionPanel(text("room.map.title"), map);

    JPanel spacer = new JPanel();
    spacer.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    right.add(characterSection, "growx");
    right.add(mapSection, "growx");
    right.add(spacer, "growy,push");
    right.add(actionButton, "growx, h 40!");

    add(right, "grow");

    // Actions
    backButton.addActionListener(e -> presenter.onBackToListClicked());

    charPrev.addActionListener(e -> presenter.onCharacterPrevious());
    charNext.addActionListener(e -> presenter.onCharacterNext());

    mapPrev.addActionListener(e -> presenter.onMapPrevious());
    mapNext.addActionListener(e -> presenter.onMapNext());

    actionButton.addActionListener(
        e -> {
          if (text("room.button.start").equals(actionButton.getText())) {
            presenter.onStartClicked();
          } else {
            presenter.onToggleReadyClicked();
          }
        });

    send.addActionListener(
        e -> {
          String msg = chatInput.getText();
          chatInput.setText("");
          presenter.onSendChat(msg);
        });

    chatInput.addActionListener(
        e -> {
          String msg = chatInput.getText();
          chatInput.setText("");
          presenter.onSendChat(msg);
        });
  }

  public void setRoom(String roomId) {
    presenter.setRoomId(roomId);
  }

  public void load() {
    presenter.loadRoom();
  }

  @Override
  public void render(RoomState roomState, boolean isHost) {
    SwingUtilities.invokeLater(
        () -> {
          updateSlots(roomState);
          updateSelectors(roomState, isHost);
          updateActionButton(isHost);
          renderChat(roomState.viewChatHistory());
        });
  }

  @Override
  public void showError(String message) {
    NotificationUtil.getInstance().show(this, Toast.Type.ERROR, message);
  }

  @Override
  public void backToList() {
    if (backToList != null) {
      backToList.run();
    }
  }

  private void updateSlots(RoomState roomState) {
    List<PlayerSlot> slots = roomState.viewSlots();
    for (int i = 0; i < slotPanels.length; i++) {
      PlayerSlot slot = slots.get(i);
      CharacterId characterId = CharacterId.fromIndex(slot.getCharacterIndex());
      slotPanels[i].setSlot(slot, characterId);
    }
  }

  private void updateSelectors(RoomState roomState, boolean isHost) {
    String localName = ApplicationContext.getInstance().getName();

    roomState.getSlots().stream()
        .filter(
            s -> !s.isEmpty() && !s.isMachine() && localName.equalsIgnoreCase(s.getPlayerName()))
        .findFirst()
        .ifPresent(
            local -> {
              CharacterId characterId = CharacterId.fromIndex(local.getCharacterIndex());
              characterValue.setText(characterId.name());
              characterValue.setToolTipText(characterId.name());
              characterAvatar.setIcon(avatarGateway.getAvatar(characterId, SELECTOR_AVATAR_SIZE));
              characterAvatar.setBackground(resolveCharacterBackground(characterId));
            });

    String mapName = MAPS[Math.floorMod(roomState.getMapIndex(), MAPS.length)];
    mapValue.setText(mapName);
    mapValue.setToolTipText(mapName);

    mapPrev.setEnabled(isHost);
    mapNext.setEnabled(isHost);
  }

  private void updateActionButton(boolean isHost) {
    actionButton.setText(isHost ? text("room.button.start") : text("room.button.ready"));
  }

  private void renderChat(List<ChatMessage> history) {
    chatPane.setText("");
    StyledDocument doc = chatPane.getStyledDocument();
    for (ChatMessage msg : history) {
      appendMessage(doc, msg);
    }
    chatPane.setCaretPosition(doc.getLength());
  }

  private void appendMessage(StyledDocument doc, ChatMessage msg) {
    SimpleAttributeSet attrs = new SimpleAttributeSet();

    if (msg.type() == ChatMessageType.SYSTEM) {
      StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
      Color systemColor = UIManager.getColor("Label.disabledForeground");
      StyleConstants.setForeground(
          attrs, systemColor != null ? systemColor : UIManager.getColor("Label.foreground"));
      StyleConstants.setFontSize(
          attrs, Math.max(11, UIManager.getFont("Label.font").getSize() - 1));
    } else {
      StyleConstants.setFontSize(attrs, UIManager.getFont("Label.font").getSize());
      if (msg.localSender()) {
        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
        Color accent = UIManager.getColor("Component.accentColor");
        StyleConstants.setForeground(
            attrs, accent != null ? accent : UIManager.getColor("Label.foreground"));
      } else {
        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(attrs, UIManager.getColor("Label.foreground"));
      }
    }

    String text;
    if (msg.type() == ChatMessageType.SYSTEM) {
      text = msg.content();
    } else {
      text = msg.senderName() + ": " + msg.content();
    }

    try {
      int start = doc.getLength();
      doc.insertString(doc.getLength(), text + "\n", attrs);
      doc.setParagraphAttributes(start, text.length(), attrs, false);
    } catch (BadLocationException ignored) {

    }
  }

  private JButton createBackButton() {
    Image image =
        ImageUtil.loadImage(ImageConstant.BACK).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
    JButton button = new JButton("", new ImageIcon(image));
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setToolTipText(text("common.back"));
    button.getAccessibleContext().setAccessibleName(text("common.back"));

    button.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:12;"
            + "margin:4,6,4,6;"
            + "borderWidth:0;"
            + "background:tint($Panel.background,2%);"
            + "hoverBackground:tint($Panel.background,6%);"
            + "pressedBackground:tint($Panel.background,10%);");
    return button;
  }

  private String text(String key) {
    try {
      return LanguageUtil.getInstance().getString(key);
    } catch (Exception e) {
      return key;
    }
  }

  private JButton createIconButton(String resourcePath) {
    Image image = ImageUtil.loadImage(resourcePath).getScaledInstance(18, 18, Image.SCALE_SMOOTH);
    JButton button = new JButton(new javax.swing.ImageIcon(image));
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.putClientProperty(FlatClientProperties.STYLE, "margin:6,6,6,6; arc:12;");
    return button;
  }

  private Color resolveCharacterBackground(CharacterId characterId) {
    if (characterId == null) {
      return UIManager.getColor("Panel.background");
    }

    Color themed = UIManager.getColor("Character." + characterId.name() + ".background");
    return themed != null ? themed : UIManager.getColor("Panel.background");
  }

  private JPanel createSectionPanel(String title, JComponent content) {
    JPanel section = new JPanel(new MigLayout("fill,insets 12,wrap", "[grow,fill]", "[][8][]"));
    section.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:16;" + "background:tint($Panel.background,2%);" + "borderWidth:0;");

    JLabel label = new JLabel(title, JLabel.CENTER);
    label.putClientProperty(FlatClientProperties.STYLE, "font:bold;");

    section.add(label, "growx");
    section.add(content, "growx");
    return section;
  }
}
