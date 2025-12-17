package vn.nghlong3004.boom.online.client.core;

import java.util.concurrent.locks.LockSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.configuration.ApplicationConfiguration;

@Slf4j
@RequiredArgsConstructor
public class GameLoop implements Runnable {

  // 1. Change to static final constants
  private static final double NANOSECONDS_PER_SECOND = 1_000_000_000.0;
  private static final int MAX_UPDATES_BEFORE_RENDER = 5;

  private final GamePanel gamePanel;

  // Calculate directly based on config
  private final double timePerFrame =
      NANOSECONDS_PER_SECOND / ApplicationConfiguration.getInstance().getFps();
  private final double timePerUpdate =
      NANOSECONDS_PER_SECOND / ApplicationConfiguration.getInstance().getUps();

  private double deltaUpdate = 0;
  private double deltaFrame = 0;

  private int frames = 0;
  private int updates = 0;

  @Override
  public void run() {
    long lastTime = System.nanoTime();
    long timer = System.currentTimeMillis();

    while (!Thread.currentThread().isInterrupted()) {
      long currentTime = System.nanoTime();
      // Calculate elapsed time in nanoseconds
      long elapsed = currentTime - lastTime;
      lastTime = currentTime;

      // Add elapsed time to deltas
      deltaUpdate += elapsed / timePerUpdate;
      deltaFrame += elapsed / timePerFrame;

      boolean didSomething = false;

      // 2. Process Update with Safety Cap
      int updateCountLoop = 0;
      while (deltaUpdate >= 1) {
        gamePanel.update();
        updates++;
        deltaUpdate--;
        didSomething = true;

        updateCountLoop++;
        if (updateCountLoop >= MAX_UPDATES_BEFORE_RENDER) {
          // Safety break: Too much lag, discard remaining updates to save the game
          deltaUpdate = 0;
          break;
        }
      }

      // 3. Process Render
      if (deltaFrame >= 1) {
        gamePanel.repaint();
        frames++;
        deltaFrame--;
        didSomething = true;
      }

      // 4. Display FPS/UPS
      if (System.currentTimeMillis() - timer > 1000) {
        log.info("FPS: {} | UPS: {}", frames, updates);
        updates = 0;
        frames = 0;
        timer += 1000;
      }

      if (!didSomething) {
        LockSupport.parkNanos(1_000_000);
        if (Thread.currentThread().isInterrupted()) {
          break;
        }
      }
    }
  }
}
