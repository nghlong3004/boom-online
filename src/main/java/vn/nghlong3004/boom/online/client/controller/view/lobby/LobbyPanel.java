package vn.nghlong3004.boom.online.client.controller.view.lobby;

import static vn.nghlong3004.boom.online.client.controller.view.room.RoomPanel.iconButton;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.gson.Gson;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import vn.nghlong3004.boom.online.client.constant.ImageConstant;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.controller.presenter.LobbyPresenter;
import vn.nghlong3004.boom.online.client.controller.view.CustomModalBorder;
import vn.nghlong3004.boom.online.client.controller.view.component.StartButton;
import vn.nghlong3004.boom.online.client.controller.view.room.RoomPanel;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.model.response.RoomPageResponse;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.model.room.RoomStatus;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.util.ImageUtil;
import vn.nghlong3004.boom.online.client.util.NotificationUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
public class LobbyPanel extends JPanel {
  private final WebSocketService webSocketService;
  private final RoomService roomService;
  private final String modalId;
  private RoomPanel roomPanel;

  private final StartButton btnCreate;
  private final StartButton btnRefresh;
  private final JButton btnBack;

  @Getter private final LobbyPresenter presenter;

  private final JPanel roomsContainer;
  private final JLabel lblPage;

  private final JButton btnPrev;
  private final JButton btnNext;

  public LobbyPanel(
      RoomService roomService, String modalId, WebSocketService webSocketService, Gson gson) {
    this.roomService = roomService;
    this.modalId = modalId;
    this.webSocketService = webSocketService;
    this.presenter = new LobbyPresenter(this, roomService, webSocketService, gson);

    setLayout(new MigLayout("fill, insets 15, wrap", "[grow,fill]", "[][grow,fill][]"));

    JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[grow][][][]"));
    header.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JLabel title = new JLabel(text("lobby.title"));
    title.putClientProperty(FlatClientProperties.STYLE, "font:bold +3;");

    this.btnCreate = new StartButton(text("lobby.btn.create"), true);
    this.btnRefresh = new StartButton(text("lobby.btn.refresh"), false);
    this.btnBack = iconButton(ImageConstant.BACK);
    btnBack.setToolTipText(text("common.back"));
    btnCreate.addActionListener(e -> presenter.onCreateRoomClicked());
    btnRefresh.addActionListener(e -> presenter.onRefreshClicked());
    btnBack.addActionListener(e -> GameContext.getInstance().previous());

    header.add(title);
    header.add(btnCreate);
    header.add(btnRefresh);
    header.add(btnBack);

    roomsContainer = new JPanel(new MigLayout("fillx, wrap, insets 0", "[grow,fill]"));
    roomsContainer.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    JScrollPane scroll = new JScrollPane(roomsContainer);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    JPanel footer = new JPanel(new MigLayout("insets 0, fillx", "[grow][][]", "[]"));
    footer.putClientProperty(FlatClientProperties.STYLE, "background:null;");

    lblPage = new JLabel(textFormat("lobby.pagination", 1, 1));

    btnPrev = iconButton(ImageConstant.ARROW_LEFT);
    btnNext = iconButton(ImageConstant.ARROW_RIGHT);

    btnPrev.addActionListener(e -> presenter.onPrevClicked());
    btnNext.addActionListener(e -> presenter.onNextClicked());

    footer.add(lblPage);
    footer.add(btnPrev);
    footer.add(btnNext);

    add(header, "growx");
    add(scroll, "grow");
    add(footer, "growx");

    if (ApplicationSession.getInstance().isOfflineMode()) {
      btnRefresh.setEnabled(false);
      btnNext.setEnabled(false);
      btnPrev.setEnabled(false);
    }
  }

  public void setControlsEnabled(boolean enabled) {
    SwingUtilities.invokeLater(
        () -> {
          btnCreate.setEnabled(enabled);
          btnRefresh.setEnabled(enabled);
          btnBack.setEnabled(enabled);
          btnPrev.setEnabled(enabled);
          btnNext.setEnabled(enabled);

          for (Component comp : roomsContainer.getComponents()) {
            comp.setEnabled(enabled);
          }

          this.setCursor(
              enabled ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });
  }

  public void render(RoomPageResponse page) {
    SwingUtilities.invokeLater(
        () -> {
          roomsContainer.removeAll();

          List<Room> rooms = page.getRooms();
          if (rooms == null || rooms.isEmpty()) {
            JLabel empty = new JLabel(text("lobby.empty"));
            empty.putClientProperty(
                FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
            roomsContainer.add(empty);
          } else {
            for (Room room : rooms) {
              roomsContainer.add(createRoomItem(room), "growx");
            }
          }

          int totalPages = Math.max(1, page.getTotalPages());
          lblPage.setText(textFormat("lobby.pagination", (page.getPageIndex() + 1), totalPages));

          btnPrev.setEnabled(page.getPageIndex() > 0);
          btnNext.setEnabled(page.getPageIndex() + 1 < totalPages);

          roomsContainer.revalidate();
          roomsContainer.repaint();
        });
  }

  public void openRoom(Room room) {
    roomPanel = new RoomPanel(roomService, modalId, presenter::onRefreshClicked, webSocketService);
    roomPanel.getPresenter().update(room);

    CustomModalBorder roomBorder =
        new CustomModalBorder(roomPanel, text("room.default_name.online"), null);
    ModalDialog.pushModal(roomBorder, modalId);
  }

  public void updateRoom(Room room) {
    roomPanel.getPresenter().update(room);
  }

  private JComponent createRoomItem(Room room) {
    JPanel panel = new JPanel(new MigLayout("fillx, insets 10, wrap 2", "[grow][right]"));
    panel.putClientProperty(
        FlatClientProperties.STYLE,
        "arc:12; border:1,1,1,1,$Component.borderColor,,12; background:fade($Panel.background,50%);");

    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

    String roomName = room.getName() != null ? room.getName() : text("room.default_name.online");
    String mapLabel = safeMapName(room.getMapIndex());
    String statusLabel =
        room.getStatus() == RoomStatus.PLAYING
            ? text("lobby.room.status.playing")
            : text("lobby.room.status.waiting");

    String line1 = textFormat("lobby.room.title.format", roomName, room.getId());
    String line2 =
        textFormat(
            "lobby.room.meta.format",
            room.getOwnerDisplayName(),
            mapLabel,
            statusLabel,
            room.getCurrentPlayers(),
            room.getMaxPlayers());

    JLabel lblTitle = new JLabel(line1);
    lblTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold;");

    JLabel lblMeta = new JLabel(line2);
    lblMeta.putClientProperty(FlatClientProperties.STYLE, "foreground:$Label.disabledForeground;");

    JLabel mapIcon = new JLabel(iconForMap(room.getMapIndex(), 48, 36));

    panel.add(lblTitle);
    panel.add(mapIcon, "span 1 2");
    panel.add(lblMeta, "span");

    panel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (panel.isEnabled()) {
              presenter.onRoomSelected(room.getId());
            }
          }
        });
    return panel;
  }

  public void showSuccess(String messageKey) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.SUCCESS, getText(messageKey)));
  }

  public void showError(String key) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.ERROR, text(key)));
  }

  public void showRawError(String key) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.ERROR, key));
  }

  public void showInfo(String key) {
    SwingUtilities.invokeLater(
        () -> NotificationUtil.getInstance().show(this, Toast.Type.INFO, getText(key)));
  }

  private String getText(String key) {
    return I18NUtil.getString(key);
  }

  private Icon iconForMap(int mapIndex, int w, int h) {
    int idx = Math.floorMod(mapIndex, RoomConstant.MAP_AVATARS.length);
    var img = ImageUtil.loadImage(RoomConstant.MAP_AVATARS[idx]);
    return new ImageIcon(img.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH));
  }

  private String safeMapName(int mapIndex) {
    int idx = Math.floorMod(mapIndex, RoomConstant.MAP_AVATARS.length);
    String path = RoomConstant.MAP_AVATARS[idx];
    int slash = path.lastIndexOf('/');
    String file = slash >= 0 ? path.substring(slash + 1) : path;
    return file.replace("_avatar", "").replace(".jpg", "");
  }

  private static String text(String key) {
    return I18NUtil.getString(key);
  }

  private static String textFormat(String key, Object... args) {
    return String.format(text(key), args);
  }
}
