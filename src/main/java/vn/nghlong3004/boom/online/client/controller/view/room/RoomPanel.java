package vn.nghlong3004.boom.online.client.controller.view.room;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import vn.nghlong3004.boom.online.client.constant.ImageConstant;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.controller.presenter.RoomPresenter;
import vn.nghlong3004.boom.online.client.controller.view.component.StartButton;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.model.room.ChatMessage;
import vn.nghlong3004.boom.online.client.model.room.ChatMessageType;
import vn.nghlong3004.boom.online.client.model.room.PlayerSlot;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.session.UserSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.util.ImageUtil;
import vn.nghlong3004.boom.online.client.util.NotificationUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
public class RoomPanel extends JPanel {

  private final String modalId;
  private final boolean onlineMode;
  private final Runnable onLeaveOnlineRefresh;

  @Getter private final RoomPresenter presenter;

  private JLabel lblRoomTitle;
  private JLabel lblRoomMeta;

  private final PlayerSlotPanel[] slotPanels;

  private final JLabel lblMapPreview;
  private final JLabel lblMapName;
  private final JButton btnMapLeft;
  private final JButton btnMapRight;

  private final JLabel lblCharacterPreview;
  private final JLabel lblCharacterName;
  private final JButton btnCharLeft;
  private final JButton btnCharRight;

  private final StartButton btnAction;

  private DefaultListModel<ChatMessage> chatModel;
  private JList<ChatMessage> chatList;
  private JTextField txtChat;
  private JScrollPane chatScroll;

  private final Map<Integer, Icon> mapPreviewCache = new HashMap<>();
  private final Map<Integer, Icon> characterPreviewCache = new HashMap<>();
  private int lastMapIndex = Integer.MIN_VALUE;
  private int lastCharacterIndex = Integer.MIN_VALUE;

  private boolean forceScrollNextRender = false;

  public RoomPanel(
      RoomService roomService, String modalId, boolean onlineMode, Runnable onLeaveOnlineRefresh) {
    this.modalId = modalId;
    this.onlineMode = onlineMode;
    this.onLeaveOnlineRefresh = onLeaveOnlineRefresh;
    this.presenter = new RoomPresenter(this, roomService, onlineMode);

    setOpaque(false);
    setLayout(new MigLayout("fill, insets 16, wrap", "[grow,fill]", "[][grow,fill]"));

    add(buildHeader(), "growx");

    JPanel body =
        new JPanel(
            new MigLayout(
                "fill, insets 0", "[grow 70,fill,min:720][grow 30,fill,min:340]", "[grow,fill]"));
    body.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JPanel left =
        new JPanel(
            new MigLayout(
                "fill, insets 0, wrap 2",
                "[grow,fill,sg slotW][grow,fill,sg slotW]",
                "[0:0,grow 25,fill][0:0,grow 25,fill][0:0,grow 50,fill]"));
    left.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    slotPanels = new PlayerSlotPanel[4];
    for (int i = 0; i < 4; i++) {
      slotPanels[i] = new PlayerSlotPanel();
      left.add(slotPanels[i], "grow, push, hmin 0");
    }

    left.add(buildChatCard(), "span 2, grow, push, hmin 0");

    JPanel right = new JPanel(new MigLayout("fill, insets 0, wrap", "[grow,fill]", "[][grow][]"));
    right.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JPanel mapCard =
        glassCard(text("room.map.title"), "fill, insets 14, wrap", "[grow,fill]", "[][grow][]");
    JPanel mapRow = new JPanel(new MigLayout("fillx, insets 0", "[][grow,fill][]", "[grow]"));
    mapRow.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    btnMapLeft = iconButton(ImageConstant.ARROW_LEFT);
    btnMapRight = iconButton(ImageConstant.ARROW_RIGHT);
    btnMapLeft.addActionListener(e -> presenter.onMapLeft());
    btnMapRight.addActionListener(e -> presenter.onMapRight());

    lblMapPreview = new JLabel();
    lblMapPreview.setHorizontalAlignment(SwingConstants.CENTER);

    mapRow.add(btnMapLeft, "w 40!, h 40!");
    mapRow.add(lblMapPreview, "grow");
    mapRow.add(btnMapRight, "w 40!, h 40!");

    lblMapName = new JLabel(" ", SwingConstants.CENTER);
    lblMapName.putClientProperty(
        FlatClientProperties.STYLE, "foreground:$Label.disabledForeground; font: -1;");

    mapCard.add(mapRow, "grow");
    mapCard.add(lblMapName, "growx");

    JPanel characterCard =
        glassCard(
            text("room.character.title"), "fill, insets 14, wrap", "[grow,fill]", "[][grow][]");
    JPanel charRow = new JPanel(new MigLayout("fillx, insets 0", "[][grow,fill][]", "[grow]"));
    charRow.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    btnCharLeft = iconButton(ImageConstant.ARROW_LEFT);
    btnCharRight = iconButton(ImageConstant.ARROW_RIGHT);
    btnCharLeft.addActionListener(e -> presenter.onCharacterLeft());
    btnCharRight.addActionListener(e -> presenter.onCharacterRight());

    lblCharacterPreview = new JLabel();
    lblCharacterPreview.setHorizontalAlignment(SwingConstants.CENTER);

    charRow.add(btnCharLeft, "w 40!, h 40!");
    charRow.add(lblCharacterPreview, "grow");
    charRow.add(btnCharRight, "w 40!, h 40!");

    lblCharacterName = new JLabel(" ", SwingConstants.CENTER);
    lblCharacterName.putClientProperty(
        FlatClientProperties.STYLE, "foreground:$Label.disabledForeground; font: -1;");

    characterCard.add(charRow, "grow");
    characterCard.add(lblCharacterName, "growx");

    btnAction = new StartButton(text("room.btn.ready"), true);
    btnAction.putClientProperty(
        FlatClientProperties.STYLE,
        "margin:14,20,14,20; arc:18; font:bold +2;"
            + "background:$Component.accentColor;"
            + "hoverBackground:fade($Component.accentColor,85%);"
            + "pressedBackground:fade($Component.accentColor,70%);");
    applyButtonUx(btnAction);

    right.add(mapCard, "growx, h 40%");
    right.add(characterCard, "grow, h 40%");
    right.add(btnAction, "growx, h 20%");

    body.add(left, "grow, push");
    body.add(right, "grow, push");

    add(body, "grow, push");

    btnAction.addActionListener(
        e -> {
          if (isMeHost(lastRenderedRoom)) {
            presenter.onStartClicked();
          } else {
            presenter.onToggleReadyClicked();
          }
        });
  }

  private Room lastRenderedRoom;

  public void renderRoom(Room room) {
    lastRenderedRoom = room;
    SwingUtilities.invokeLater(
        () -> {
          if (room == null) return;

          String fallbackName =
              onlineMode ? text("room.default_name.online") : text("room.default_name.offline");
          lblRoomTitle.setText(room.getName() != null ? room.getName() : fallbackName);
          lblRoomMeta.setText(
              textFormat(
                  "room.meta.format",
                  text("room.label.id"),
                  room.getId(),
                  room.getCurrentPlayers(),
                  room.getMaxPlayers()));

          Long myId =
              UserSession.getInstance().getCurrentUser() != null
                  ? UserSession.getInstance().getCurrentUser().getId()
                  : null;

          List<PlayerSlot> slots = room.getSlots();
          for (int i = 0; i < slotPanels.length; i++) {
            PlayerSlot slot = (slots != null && i < slots.size()) ? slots.get(i) : null;
            slotPanels[i].setSlot(slot, myId);
          }

          int mapIdx = Math.floorMod(room.getMapIndex(), RoomConstant.MAP_AVATARS.length);
          if (mapIdx != lastMapIndex) {
            lblMapPreview.setIcon(getMapPreviewIcon(mapIdx));
            lastMapIndex = mapIdx;
          }
          lblMapName.setText(safeName(RoomConstant.MAP_AVATARS[mapIdx]));

          boolean canChangeMap =
              myId != null && room.getOwnerId() != null && myId.equals(room.getOwnerId());
          btnMapLeft.setEnabled(canChangeMap);
          btnMapRight.setEnabled(canChangeMap);

          int myChar = getMyCharacterIndex(room, myId);
          int charIdx = Math.floorMod(myChar, RoomConstant.PLAYER_AVATARS.length);
          if (charIdx != lastCharacterIndex) {
            lblCharacterPreview.setIcon(getCharacterPreviewIcon(charIdx));
            lastCharacterIndex = charIdx;
          }
          lblCharacterName.setText(safeName(RoomConstant.PLAYER_AVATARS[charIdx]));

          btnCharLeft.setEnabled(myId != null);
          btnCharRight.setEnabled(myId != null);

          if (canChangeMap) {
            btnAction.setText(text("room.btn.start"));
          } else {
            boolean ready = isMeReady(room, myId);
            btnAction.setText(ready ? text("room.btn.unready") : text("room.btn.ready"));
          }

          if (chatModel != null) {
            boolean shouldAutoScroll = forceScrollNextRender || isChatNearBottom(48);
            chatModel.clear();
            if (room.getChat() != null) {
              for (ChatMessage m : room.getChat()) {
                chatModel.addElement(m);
              }
            }
            if (shouldAutoScroll) {
              scrollChatToBottom();
            }
            forceScrollNextRender = false;
          }
        });
  }

  private boolean isChatNearBottom(int thresholdPx) {
    if (chatScroll == null) return true;
    JScrollBar bar = chatScroll.getVerticalScrollBar();
    if (bar == null) return true;

    int value = bar.getValue();
    int extent = bar.getModel().getExtent();
    int max = bar.getMaximum();
    return value + extent >= max - thresholdPx;
  }

  public int getMyCharacterIndex(Room room, Long myId) {
    if (room == null || myId == null || room.getSlots() == null) return 0;
    for (PlayerSlot slot : room.getSlots()) {
      if (slot != null && slot.isOccupied() && myId.equals(slot.getUserId())) {
        return slot.getCharacterIndex();
      }
    }
    return 0;
  }

  public void backToHome() {
    SwingUtilities.invokeLater(
        () -> {
          ModalDialog.closeModal(modalId);
          GameContext.getInstance().previous();
        });
  }

  public void backToLobby() {
    SwingUtilities.invokeLater(
        () -> {
          ModalDialog.popModel(modalId);
          if (onLeaveOnlineRefresh != null) {
            onLeaveOnlineRefresh.run();
          }
        });
  }

  public void showInfoUpcoming() {
    SwingUtilities.invokeLater(
        () ->
            NotificationUtil.getInstance()
                .show(this, Toast.Type.INFO, text("common.feature.upcoming")));
  }

  private JPanel buildHeader() {
    JPanel header = new JPanel(new MigLayout("fillx, insets 0", "[grow,fill][right]", "[]"));
    header.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JPanel left = new JPanel(new MigLayout("insets 0, wrap", "[grow,fill]", "[][]"));
    left.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    lblRoomTitle =
        new JLabel(
            onlineMode ? text("room.default_name.online") : text("room.default_name.offline"));
    lblRoomTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +4;");

    lblRoomMeta = new JLabel(" ");
    lblRoomMeta.putClientProperty(
        FlatClientProperties.STYLE, "foreground:$Label.disabledForeground;");

    left.add(lblRoomTitle);
    left.add(lblRoomMeta);

    JButton btnBack = iconButton(ImageConstant.BACK);
    btnBack.setToolTipText(text("common.back"));
    btnBack.addActionListener(e -> presenter.onBackClicked());

    header.add(left);
    header.add(btnBack, "w 44!, h 44!");

    return header;
  }

  private JPanel buildChatCard() {
    JPanel card =
        glassCard(
            text("room.chat.title"), "fill, insets 14, wrap", "[grow,fill]", "[][grow,fill][]");

    chatModel = new DefaultListModel<>();
    chatList = new JList<>(chatModel);
    chatList.setFocusable(false);
    // Target at least 7-8 lines visible without scrolling.
    chatList.setVisibleRowCount(8);
    chatList.setFixedCellHeight(-1);
    chatList.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    chatScroll = new JScrollPane(chatList);
    chatScroll.setBorder(BorderFactory.createEmptyBorder());
    chatScroll.getViewport().setOpaque(false);
    chatScroll.setOpaque(false);
    chatScroll.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JPanel input = new JPanel(new MigLayout("fillx, insets 0", "[grow,fill][]", "[]"));
    input.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    txtChat = new JTextField();
    txtChat.putClientProperty(FlatClientProperties.STYLE, "arc:14; margin:8,10,8,10;");
    txtChat.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, text("room.chat.placeholder"));

    JButton btnSend = new JButton(text("room.chat.send"));
    btnSend.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:14; margin:8,14,8,14; font:bold;"
            + "background:fade($Panel.background,35%);"
            + "hoverBackground:fade($Panel.background,60%);"
            + "pressedBackground:fade($Panel.background,75%);");
    applyButtonUx(btnSend);

    ActionListener send =
        e -> {
          String content = txtChat.getText();
          if (content == null || content.trim().isEmpty()) return;
          forceScrollNextRender = true;
          presenter.onSendChat(content.trim());
          txtChat.setText("");
        };
    btnSend.addActionListener(send);
    txtChat.addActionListener(send);

    installChatListRenderer();

    input.add(txtChat);
    input.add(btnSend, "w 76!");

    // Let the scroll pane fully occupy the remaining space.
    card.add(chatScroll, "grow, push");
    card.add(input, "growx");

    return card;
  }

  private void scrollChatToBottom() {
    if (chatList == null || chatModel == null) return;

    int lastIndex = chatModel.getSize() - 1;
    if (lastIndex < 0) return;

    // Ensure the newest item is visible.
    chatList.ensureIndexIsVisible(lastIndex);
    if (chatScroll != null) {
      JScrollBar bar = chatScroll.getVerticalScrollBar();
      if (bar != null) {
        bar.setValue(bar.getMaximum());
      }
    }
  }

  private Icon getMapPreviewIcon(int mapIdx) {
    return mapPreviewCache.computeIfAbsent(
        mapIdx, idx -> scaledIcon(RoomConstant.MAP_AVATARS[idx], 220, 130));
  }

  private Icon getCharacterPreviewIcon(int charIdx) {
    return characterPreviewCache.computeIfAbsent(
        charIdx, idx -> scaledIcon(RoomConstant.PLAYER_AVATARS[idx], 120, 120));
  }

  private static Icon scaledIcon(String resourcePath, int w, int h) {
    var img = ImageUtil.loadImage(resourcePath);
    return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
  }

  private void installChatListRenderer() {
    chatList.setCellRenderer(
        (list, value, index, isSelected, cellHasFocus) -> {
          JLabel lbl = new JLabel();
          lbl.setOpaque(false);
          lbl.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

          if (value == null) {
            lbl.setText(" ");
            return lbl;
          }

          if (value.getType() == ChatMessageType.SYSTEM) {
            lbl.putClientProperty(
                FlatClientProperties.STYLE, "foreground:$Label.disabledForeground;");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setText(escape(value.getContent()));
            return lbl;
          }

          Long myId =
              UserSession.getInstance().getCurrentUser() != null
                  ? UserSession.getInstance().getCurrentUser().getId()
                  : null;
          boolean isMe = myId != null && myId.equals(value.getSenderId());

          lbl.setHorizontalAlignment(SwingConstants.LEFT);
          String time =
              value.getCreated() != null
                  ? TIME_FMT.format(value.getCreated().atZone(ZoneId.systemDefault()))
                  : "";
          String name = value.getSenderDisplayName() != null ? value.getSenderDisplayName() : "";
          String content = value.getContent() != null ? value.getContent() : "";

          String color = isMe ? "$Component.accentColor" : "$Label.foreground";
          lbl.putClientProperty(FlatClientProperties.STYLE, "foreground:" + color + ";");
          lbl.setText(
              "<html><b>"
                  + escape(name)
                  + "</b> <span style='opacity:0.65'>"
                  + escape(time)
                  + "</span><br/>"
                  + escape(content)
                  + "</html>");

          return lbl;
        });
  }

  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

  private static JPanel glassCard(String title, String layout, String col, String row) {
    JPanel card = new JPanel(new MigLayout(layout, col, row));
    card.setOpaque(false);
    card.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:20; border:1,1,1,1,fade($Component.borderColor,70%),,20; background:fade($Panel.background,55%);");

    JLabel t = new JLabel(title);
    t.putClientProperty(FlatClientProperties.STYLE, "font:bold +1;");
    card.add(t, "growx");

    return card;
  }

  private static JButton iconButton(String resPath) {
    var img = ImageUtil.loadImage(resPath);
    Icon icon = new ImageIcon(img.getScaledInstance(18, 18, Image.SCALE_SMOOTH));

    JButton button = new JButton(icon);
    applyButtonUx(button);
    return button;
  }

  private static void applyButtonUx(AbstractButton b) {
    if (b == null) return;
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  private boolean isMeHost(Room room) {
    if (room == null) return false;
    Long myId =
        UserSession.getInstance().getCurrentUser() != null
            ? UserSession.getInstance().getCurrentUser().getId()
            : null;
    return myId != null && room.getOwnerId() != null && myId.equals(room.getOwnerId());
  }

  private boolean isMeReady(Room room, Long myId) {
    if (room == null || myId == null || room.getSlots() == null) return false;
    for (PlayerSlot slot : room.getSlots()) {
      if (slot != null && slot.isOccupied() && myId.equals(slot.getUserId())) {
        return slot.isHost() || slot.isReady();
      }
    }
    return false;
  }

  private static String safeName(String path) {
    if (path == null) return "";
    int slash = path.lastIndexOf('/');
    String file = slash >= 0 ? path.substring(slash + 1) : path;
    int dot = file.lastIndexOf('.');
    if (dot > 0) file = file.substring(0, dot);
    return file.replace("_avatar", "").replace('_', ' ');
  }

  private static String escape(String s) {
    if (s == null) return "";
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  private static String text(String key) {
    return I18NUtil.getString(key);
  }

  private static String textFormat(String key, Object... args) {
    return String.format(text(key), args);
  }
}
