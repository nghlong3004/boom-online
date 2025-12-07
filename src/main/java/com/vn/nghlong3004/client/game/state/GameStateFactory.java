package com.vn.nghlong3004.client.game.state;

import static com.vn.nghlong3004.client.constant.ButtonConstant.MENU_BUTTON_HEIGHT;
import static com.vn.nghlong3004.client.constant.ButtonConstant.MENU_BUTTON_WIDTH;
import static com.vn.nghlong3004.client.constant.GameConstant.GAME_HEIGHT;
import static com.vn.nghlong3004.client.constant.GameConstant.GAME_WIDTH;

import com.vn.nghlong3004.client.constant.ImageConstant;
import com.vn.nghlong3004.client.game.GamePanel;
import com.vn.nghlong3004.client.util.ImageUtil;
import com.vn.nghlong3004.client.util.LanguageUtil;
import com.vn.nghlong3004.client.view.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import raven.modal.ModalDialog;
import raven.modal.option.Option;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public final class GameStateFactory {

  public static Map<GameStateType, GameState> createStateMap(GamePanel gamePanel) {
    Map<GameStateType, GameState> stateMap = new EnumMap<>(GameStateType.class);
    stateMap.put(GameStateType.WELCOME, createWelcomeState(gamePanel));
    return stateMap;
  }

  private static GameState createWelcomeState(GamePanel gamePanel) {
    Option option =
        ModalDialog.createOption()
            .setCloseOnPressedEscape(false)
            .setAnimationEnabled(true)
            .setOpacity(0.5f)
            .setSliderDuration(600);
    String icon = "images/account.svg";
    LoginPanel login = new LoginPanel();
    CustomModalBorder loginPanel = new CustomModalBorder(login, "Sign In", icon);
    icon = "images/register.svg";
    CustomModalBorder registerPanel = new CustomModalBorder(new RegisterPanel(), "Sign Up", icon);

    icon = "images/forgot_password.svg";
    CustomModalBorder forgotPasswordPanel =
        new CustomModalBorder(new ForgotPasswordPanel(), "Forgot Password", icon);

    login.setRegisterPanel(registerPanel);
    login.setForgotPasswordPanel(forgotPasswordPanel);
    int x = GAME_WIDTH - MENU_BUTTON_WIDTH >>> 1;
    int y = GAME_HEIGHT - MENU_BUTTON_HEIGHT >>> 1;
    ButtonAdapter buttonAdapter =
        new TextButton(x, y, LanguageUtil.getInstance().getString("welcome_button"));
    BufferedImage background = ImageUtil.loadImage(ImageConstant.WELCOME_BACKGROUND);
    return WelcomeState.builder()
        .gamePanel(gamePanel)
        .loginPanel(loginPanel)
        .option(option)
        .buttonAdapter(buttonAdapter)
        .background(background)
        .build();
  }
}
