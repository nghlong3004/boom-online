package com.vn.nghlong3004.client.util;

import com.vn.nghlong3004.client.constant.NotificationConstant;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import raven.modal.Toast;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/7/2025
 */
public class NotificationUtil {

  private final AtomicInteger countNotification;

  public static NotificationUtil getInstance() {
    return Holder.INSTANCE;
  }

  private NotificationUtil() {
    countNotification = new AtomicInteger(0);
  }

  public void show(Component owner, Toast.Type type, String message) {
    if (owner != null) {
      if (countNotification.incrementAndGet() >= NotificationConstant.MAX_NOTIFICATION) {
        countNotification.set(0);
        Toast.closeAll();
      }
      Toast.show(owner, type, message);
    }
  }

  private static class Holder {
    private static final NotificationUtil INSTANCE = new NotificationUtil();
  }
}
