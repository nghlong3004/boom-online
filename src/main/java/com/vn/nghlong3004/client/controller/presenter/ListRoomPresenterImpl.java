package com.vn.nghlong3004.client.controller.presenter;

import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.context.GameContext;
import com.vn.nghlong3004.client.controller.ListRoomPresenter;
import com.vn.nghlong3004.client.controller.ListRoomView;
import com.vn.nghlong3004.client.model.room.RoomMode;
import com.vn.nghlong3004.client.model.room.RoomState;
import com.vn.nghlong3004.client.model.room.RoomSummary;
import com.vn.nghlong3004.client.service.RoomService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/14/2025
 */
@RequiredArgsConstructor
public class ListRoomPresenterImpl implements ListRoomPresenter {

  private static final int PAGE_SIZE = 3;

  private final ListRoomView view;
  private final RoomService roomService;

  private int pageIndex = 0;
  private List<RoomSummary> cached = Collections.emptyList();

  @Override
  public void loadRooms() {
    cached = roomService.listRooms();
    pageIndex = Math.max(0, Math.min(pageIndex, getTotalPages() - 1));
    renderPage();
  }

  @Override
  public void nextPage() {
    int total = getTotalPages();
    if (total <= 0) {
      return;
    }
    pageIndex = Math.min(pageIndex + 1, total - 1);
    renderPage();
  }

  @Override
  public void previousPage() {
    pageIndex = Math.max(0, pageIndex - 1);
    renderPage();
  }

  @Override
  public void onCreateRoomClicked() {
    String localName = getLocalName();
    RoomState roomState = roomService.createRoom(localName, RoomMode.ONLINE);
    view.openRoom(roomState);
  }

  @Override
  public void onBackClicked() {
    GameContext.getInstance().previousState();
  }

  @Override
  public void onJoinRoomDoubleClicked(String roomId) {
    try {
      String localName = getLocalName();
      RoomState roomState = roomService.joinRoom(roomId, localName);
      view.openRoom(roomState);
    } catch (Exception e) {
      view.showError(e.getMessage());
    }
  }

  private void renderPage() {
    int totalPages = getTotalPages();
    if (totalPages <= 0) {
      view.showRooms(Collections.emptyList(), 0, 0);
      return;
    }

    int from = pageIndex * PAGE_SIZE;
    int to = Math.min(from + PAGE_SIZE, cached.size());
    List<RoomSummary> slice = cached.subList(from, to);
    view.showRooms(slice, pageIndex, totalPages);
  }

  private int getTotalPages() {
    if (cached.isEmpty()) {
      return 0;
    }
    return (cached.size() + PAGE_SIZE - 1) / PAGE_SIZE;
  }

  private String getLocalName() {
    String email = ApplicationContext.getInstance().getEmail();
    if (email != null && !email.isBlank()) {
      return email;
    }
    return "Guest";
  }
}
