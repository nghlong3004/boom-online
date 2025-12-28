package vn.nghlong3004.boom.online.client.core.state;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import lombok.extern.slf4j.Slf4j;
import raven.modal.ModalDialog;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.model.map.GameMap;
import vn.nghlong3004.boom.online.client.renderer.MapRenderer;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.PlayingSession;

@Slf4j
public class PlayingState implements GameState {

    private final MapRenderer mapRenderer;
    private boolean initialized;

    public PlayingState() {
        this.mapRenderer = new MapRenderer();
        this.initialized = false;
    }

    @Override
    public void next(GameContext gameContext) {
        gameContext.changeState(GameStateType.PLAYING);
    }

    @Override
    public void previous(GameContext gameContext) {
        PlayingSession.getInstance().clear();
        initialized = false;
        gameContext.changeState(GameStateType.START);
    }

    @Override
    public void update() {
        if (!initialized) {
            initialized = true;
            closeAllModals();
            log.info("Game started - Map: {}", getGameMap().getMapType().getName());
        }
    }

    @Override
    public void render(Graphics g) {
        GameMap gameMap = getGameMap();
        if (gameMap != null) {
            mapRenderer.render(g, gameMap);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            previous(GameContext.getInstance());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        GameState.super.keyReleased(e);
    }

    private GameMap getGameMap() {
        return PlayingSession.getInstance().getGameMap();
    }

    private void closeAllModals() {
        String startId = ApplicationSession.getInstance().getStartId();
        if (ModalDialog.isIdExist(startId)) {
            ModalDialog.closeModal(startId);
        }
    }
}
