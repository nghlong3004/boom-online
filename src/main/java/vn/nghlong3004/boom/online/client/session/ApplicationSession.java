package vn.nghlong3004.boom.online.client.session;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Getter
@Setter
public class ApplicationSession {

  private boolean offlineMode;

  private final String welcomeId;
  private final String homeId;
  private final String startId;

  private ApplicationSession() {
    welcomeId = UUID.randomUUID().toString();
    homeId = UUID.randomUUID().toString();
    startId = UUID.randomUUID().toString();
  }

  public static ApplicationSession getInstance() {
    return ApplicationSession.Holder.INSTANCE;
  }

  private static class Holder {
    private static final ApplicationSession INSTANCE = new ApplicationSession();
  }
}
