package vn.nghlong3004.boom.online.client.controller.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.controller.view.lobby.LobbyPanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.response.ErrorResponse;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
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
  private final WebSocketService webSocketService;
  private final Gson gson;

  private int pageIndex;
  private boolean isProcessing = false;

  public void init() {
    loadPage(0);
  }

  private void startProcessing() {
    this.isProcessing = true;
    view.setControlsEnabled(false);
  }

  private void endProcessing() {
    this.isProcessing = false;
    view.setControlsEnabled(true);
  }

  public void onRefreshClicked() {
    if (isProcessing) return;
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
    if (isProcessing) return;

    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) return;

    startProcessing();
    view.showInfo("common.processing");

    String defaultName =
        I18NUtil.getString("room.base_name").formatted(currentUser.getDisplayName());

    roomService
        .createRoom(currentUser, defaultName)
        .thenAccept(
            room -> {
              handleCreateRoom(room);
              endProcessing();
            })
        .exceptionally(
            ex -> {
              handleException(ex);
              endProcessing();
              return null;
            });
  }

  public void onRoomSelected(String roomId) {
    if (isProcessing) return;

    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) return;

    startProcessing();
    view.showInfo("common.processing");

    roomService
        .joinRoom(roomId, currentUser)
        .thenAccept(
            room -> {
              handleJoinRoom(room);
              endProcessing();
            })
        .exceptionally(
            ex -> {
              handleException(ex);
              endProcessing();
              return null;
            });
  }

  private void loadPage(int targetPage) {
    if (isProcessing || webSocketService == null) return;
    if (targetPage < 0) targetPage = 0;

    startProcessing();
    final int finalPage = targetPage;

    roomService
        .rooms(finalPage, PAGE_SIZE)
        .thenAccept(
            pageResponse -> {
              SwingUtilities.invokeLater(
                  () -> {
                    int totalPages = Math.max(1, pageResponse.getTotalPages());
                    if (finalPage >= totalPages) {
                      isProcessing = false;
                      loadPage(totalPages - 1);
                    } else {
                      this.pageIndex = pageResponse.getPageIndex();
                      view.render(pageResponse);
                      endProcessing();
                    }
                  });
            })
        .exceptionally(
            ex -> {
              log.error("Error loading rooms", ex);
              endProcessing();
              return null;
            });
  }

  private void handleCreateRoom(Room room) {
    view.showSuccess("room.create.success");
    view.openRoom(room);
    if (ApplicationSession.getInstance().isOfflineMode()) {
      return;
    }
    handleRoomConnection(room);
  }

  private void handleJoinRoom(Room room) {
    view.showSuccess("room.join.success");
    view.openRoom(room);
    if (ApplicationSession.getInstance().isOfflineMode()) {
      return;
    }
    handleRoomConnection(room);
  }

  private void handleRoomConnection(Room room) {
    String token = UserSession.getInstance().getAccessToken();
    try {
      webSocketService.connectAndSubscribe(
          token,
          room.getId(),
          updatedRoom -> SwingUtilities.invokeLater(() -> view.updateRoom(updatedRoom)));

    } catch (Exception e) {
      log.error("Failed to establish WebSocket connection", e);
      SwingUtilities.invokeLater(() -> view.showRawError("WebSocket Error: " + e.getMessage()));
    }
  }

  private void handleException(Throwable ex) {
    SwingUtilities.invokeLater(
        () -> {
          log.error("Action failed", ex);
          Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
          String messageKey = mapErrorToMessageKey(cause.getMessage());

          if (messageKey.startsWith("Raw:")) {
            view.showRawError(messageKey.substring(4));
          } else {
            view.showError(messageKey);
          }
        });
  }

  private String mapErrorToMessageKey(String errorBody) {
    if (errorBody == null) return "common.error.server";

    if (errorBody.contains("ConnectException") || errorBody.contains("Network")) {
      return "common.error.server";
    }

    try {
      ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);

      if (errorResponse != null && errorResponse.code() != null) {
        return switch (errorResponse.code()) {
          case "RoomFull" -> "room.join.full";
          case "RoomPlaying" -> "room.join.playing";
          case "RoomNotFound" -> "room.join.not_found";
          case "InvalidRequest" -> "common.error.invalid_request";
          default -> "Raw:" + errorResponse.message();
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.warn("Cannot parse error body as JSON: {}", errorBody);
    }

    return "Raw:" + errorBody;
  }
}
