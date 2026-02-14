package vn.nghlong3004.boom.online.client.core;

import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.GameConstant;
import vn.nghlong3004.boom.online.client.constant.ImageConstant;
import vn.nghlong3004.boom.online.client.util.ImageUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Slf4j
@RequiredArgsConstructor
public class GameCanvas extends JFrame {
  private final Thread thread;

  protected void start() {
    thread.start();
    setVisible(true);
  }

  protected void configure(JPanel panel) {
    setTitle(GameConstant.TITLE);
    add(panel);
    setResizable(false);
    pack();
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setIconImage(ImageUtil.loadImage(ImageConstant.IMAGE_TITLE));
  }

  protected void registerShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  log.info("Starting shutdown process...");
                  try {
                    if (thread != null && thread.isAlive()) {
                      thread.interrupt();
                      thread.join(3000);
                      if (thread.isAlive()) {
                        log.warn("Game thread did not stop in time.");
                      } else {
                        log.info("Game thread stopped successfully.");
                      }
                    }
                  } catch (InterruptedException e) {
                    log.error("Shutdown interrupted", e);
                    Thread.currentThread().interrupt();
                  } finally {
                    log.info("Application closed.");
                  }
                },
                "Shutdown-Thread"));
  }
}
