package vn.nghlong3004.boom.online.client;

import javax.swing.*;
import vn.nghlong3004.boom.online.client.core.GameLaunch;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/3/2025
 */
public class BoomOnlineClient {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(GameLaunch::run);
  }
}
