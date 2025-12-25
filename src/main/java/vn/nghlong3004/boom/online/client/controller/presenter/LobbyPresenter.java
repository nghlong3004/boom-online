package vn.nghlong3004.boom.online.client.controller.presenter;

import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.controller.view.lobby.LobbyPanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.session.UserSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Slf4j
@RequiredArgsConstructor
public class LobbyPresenter {

  private static final int PAGE_SIZE = 3;

  private final LobbyPanel view;
  private final RoomService roomService;

  private int pageIndex;

  public void init() {
    loadPage(0);
  }

  public void onRefreshClicked() {
    view.showInfo("common.loading");
    loadPage(pageIndex);
  }

  public void onPrevClicked() {
    if (pageIndex <= 0) return;
    loadPage(pageIndex - 1);
  }

  public void onNextClicked() {
    loadPage(pageIndex + 1);
  }

  public void onCreateRoomClicked() {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) return;
    view.showInfo("common.processing");
    String defaultName =
        I18NUtil.getString("room.base_name").formatted(currentUser.getDisplayName());

    roomService
        .createRoom(currentUser, defaultName)
        .thenAccept(
            newRoom -> {
              SwingUtilities.invokeLater(
                  () -> {
                    view.showSuccess("room.create.success");
                    view.openRoom(newRoom);
                  });
            })
        .exceptionally(
            ex -> {
              SwingUtilities.invokeLater(
                  () -> {
                    log.error("Error create room: ", ex);
                    String msg =
                        ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.showRawError(msg);
                  });
              return null;
            });
  }

  public void onRoomSelected(String roomId) {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) return;
    view.showInfo("common.processing");
    Room room = roomService.joinRoom(roomId, currentUser);
    if (room != null) {
      view.openRoom(room);
    }
  }

  private void loadPage(int targetPage) {
    if (targetPage < 0) targetPage = 0;
    final int finalPage = targetPage;

    roomService
        .rooms(finalPage, PAGE_SIZE)
        .thenAccept(
            pageResponse -> {
              SwingUtilities.invokeLater(
                  () -> {
                    int totalPages = pageResponse.getTotalPages();
                    if (totalPages > 0 && finalPage >= totalPages) {
                      loadPage(totalPages - 1);
                    } else {
                      this.pageIndex = pageResponse.getPageIndex();
                      view.render(pageResponse);
                    }
                    view.showSuccess("room.refresh");
                  });
            })
        .exceptionally(
            ex -> {
              SwingUtilities.invokeLater(
                  () -> {
                    log.error("Error Load Rooms {}", ex.getMessage());
                  });
              return null;
            });
  }
}
