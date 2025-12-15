package com.vn.nghlong3004.client.game.state;

import static com.vn.nghlong3004.client.constant.ButtonConstant.BUTTON_HEIGHT;
import static com.vn.nghlong3004.client.constant.ButtonConstant.BUTTON_WIDTH;
import static com.vn.nghlong3004.client.constant.GameConstant.GAME_HEIGHT;
import static com.vn.nghlong3004.client.constant.GameConstant.GAME_WIDTH;

import com.google.gson.Gson;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.configuration.WebConfiguration;
import com.vn.nghlong3004.client.constant.ImageConstant;
import com.vn.nghlong3004.client.controller.view.CustomModalBorder;
import com.vn.nghlong3004.client.controller.view.component.TextButton;
import com.vn.nghlong3004.client.controller.view.home.SettingPanel;
import com.vn.nghlong3004.client.controller.view.welcome.*;
import com.vn.nghlong3004.client.game.GamePanel;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.ImageUtil;
import com.vn.nghlong3004.client.util.LanguageUtil;
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

  private static Option option;

  public static Map<GameStateType, GameState> createStateMap(GamePanel gamePanel) {
    Map<GameStateType, GameState> stateMap = new EnumMap<>(GameStateType.class);
    stateMap.put(GameStateType.WELCOME, createWelcomeState(gamePanel));
    stateMap.put(GameStateType.HOME, createHomeState(gamePanel));
    stateMap.put(GameStateType.START, createStartState(gamePanel));
    return stateMap;
  }

  private static GameState createStartState(GamePanel gamePanel) {
    BufferedImage background = ImageUtil.loadImage(ImageConstant.HOME_BACKGROUND);
    return StartState.builder().background(background).gamePanel(gamePanel).build();
  }

  private static GameState createHomeState(GamePanel gamePanel) {
    BufferedImage background = ImageUtil.loadImage(ImageConstant.HOME_BACKGROUND);
    TextButton[] homeButtons = new TextButton[3];

    int x = GAME_WIDTH - BUTTON_WIDTH >>> 1;
    int factor = GAME_HEIGHT - BUTTON_HEIGHT >>> 1;
    int spacing = BUTTON_HEIGHT * 5 / 4;

    String[] texts = {"START", "SETTING", "QUIT"};

    for (int i = 0; i < 3; i++) {
      int y = factor + i * spacing;
      homeButtons[i] = new TextButton(x, y, texts[i]);
    }
    Option option = createOption();
    CustomModalBorder settingPanel = createSetting();

    return HomeState.builder()
        .background(background)
        .homeButtons(homeButtons)
        .gamePanel(gamePanel)
        .settingPanel(settingPanel)
        .option(option)
        .build();
  }

  private static GameState createWelcomeState(GamePanel gamePanel) {
    Option option = createOption();
    HttpService httpService = WebConfiguration.getInstance().getHttpService();
    Gson gson = ApplicationConfiguration.getInstance().getGson();

    LoginPanel login = new LoginPanel(httpService, gson);
    ForgotPasswordPanel forgotPassword = new ForgotPasswordPanel(httpService, gson);

    String icon = "images/account.svg";
    CustomModalBorder loginPanel = new CustomModalBorder(login, "Sign In", icon);

    icon = "images/register.svg";
    CustomModalBorder registerPanel =
        new CustomModalBorder(new RegisterPanel(httpService, gson), "Sign Up", icon);

    icon = "images/forgot_password.svg";
    CustomModalBorder forgotPasswordPanel =
        new CustomModalBorder(forgotPassword, "Forgot Password", icon);

    icon = "images/forgot_password.svg";
    CustomModalBorder resetPasswordPanel =
        new CustomModalBorder(new ResetPasswordPanel(httpService, gson), "Reset password.", icon);

    login.setRegisterPanel(registerPanel);
    login.setForgotPasswordPanel(forgotPasswordPanel);
    forgotPassword.setResetPasswordPanel(resetPasswordPanel);
    int x = GAME_WIDTH - BUTTON_WIDTH >>> 1;
    int y = GAME_HEIGHT - BUTTON_HEIGHT >>> 1;
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

  private static CustomModalBorder createSetting() {
    String icon = "images/setting.svg";
    return new CustomModalBorder(new SettingPanel(), "Setting", icon);
  }

  private static Option createOption() {
    if (option == null) {
      option =
          ModalDialog.createOption()
              .setCloseOnPressedEscape(false)
              .setAnimationEnabled(true)
              .setOpacity(0.5f)
              .setSliderDuration(600);
    }
    return option;
  }
}
