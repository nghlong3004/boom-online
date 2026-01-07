package vn.nghlong3004.boom.online.client.core.state;

import static vn.nghlong3004.boom.online.client.constant.ButtonConstant.BUTTON_HEIGHT;
import static vn.nghlong3004.boom.online.client.constant.ButtonConstant.BUTTON_WIDTH;
import static vn.nghlong3004.boom.online.client.constant.GameConstant.GAME_HEIGHT;
import static vn.nghlong3004.boom.online.client.constant.GameConstant.GAME_WIDTH;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import raven.modal.ModalDialog;
import raven.modal.option.Option;
import vn.nghlong3004.boom.online.client.constant.ImageConstant;
import vn.nghlong3004.boom.online.client.controller.view.CustomModalBorder;
import vn.nghlong3004.boom.online.client.controller.view.SettingPanel;
import vn.nghlong3004.boom.online.client.controller.view.component.TextButton;
import vn.nghlong3004.boom.online.client.controller.view.welcome.ForgotPasswordPanel;
import vn.nghlong3004.boom.online.client.controller.view.welcome.LoginPanel;
import vn.nghlong3004.boom.online.client.controller.view.welcome.RegisterPanel;
import vn.nghlong3004.boom.online.client.controller.view.welcome.ResetPasswordPanel;
import vn.nghlong3004.boom.online.client.core.GameObjectContainer;
import vn.nghlong3004.boom.online.client.core.GamePanel;
import vn.nghlong3004.boom.online.client.service.HttpService;
import vn.nghlong3004.boom.online.client.util.I18NUtil;
import vn.nghlong3004.boom.online.client.util.ImageUtil;

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
    stateMap.put(GameStateType.PLAYING, createPlayingState(gamePanel));
    return stateMap;
  }

  private static GameState createPlayingState(GamePanel gamePanel) {
    return new PlayingState(gamePanel, GameObjectContainer.getGson());
  }

  private static GameState createStartState(GamePanel gamePanel) {
    int x = (GAME_WIDTH - BUTTON_WIDTH) / 2;
    int y = (GAME_HEIGHT - BUTTON_HEIGHT) / 4;
    TextButton textButton = new TextButton(x, y, I18NUtil.getString("lobby.btn.back_lobby"));
    BufferedImage background = ImageUtil.loadImage(ImageConstant.SETTING_BACKGROUND);
    return StartState.builder()
        .background(background)
        .gamePanel(gamePanel)
        .textButton(textButton)
        .option(createOption())
        .build();
  }

  private static GameState createHomeState(GamePanel gamePanel) {
    BufferedImage background = ImageUtil.loadImage(ImageConstant.HOME_BACKGROUND);
    TextButton[] homeButtons = new TextButton[3];

    int x = (GAME_WIDTH - BUTTON_WIDTH) / 2;
    int factor = (GAME_HEIGHT - BUTTON_HEIGHT) / 2;
    int spacing = (BUTTON_HEIGHT * 5) / 4;

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
    HttpService httpService = GameObjectContainer.getHttpService();
    Gson gson = GameObjectContainer.getGson();

    LoginPanel login = new LoginPanel(httpService, gson);
    ForgotPasswordPanel forgotPassword = new ForgotPasswordPanel(httpService, gson);
    RegisterPanel register = new RegisterPanel(httpService, gson, login.getPresenter());
    ResetPasswordPanel resetPassword =
        new ResetPasswordPanel(httpService, gson, forgotPassword.getPresenter());

    CustomModalBorder loginPanel =
        new CustomModalBorder(login, "Sign In", ImageConstant.LOGIN_ICON);

    CustomModalBorder registerPanel =
        new CustomModalBorder(register, "Sign Up", ImageConstant.REGISTER_ICON);

    CustomModalBorder forgotPasswordPanel =
        new CustomModalBorder(
            forgotPassword, "Forgot Password", ImageConstant.FORGOT_PASSWORD_ICON);

    CustomModalBorder resetPasswordPanel =
        new CustomModalBorder(resetPassword, "Reset password.", ImageConstant.RESET_PASSWORD_ICON);

    login.setRegisterPanel(registerPanel);
    login.setForgotPasswordPanel(forgotPasswordPanel);
    forgotPassword.setResetPasswordPanel(resetPasswordPanel);

    int x = (GAME_WIDTH - BUTTON_WIDTH) / 2;
    int y = (GAME_HEIGHT - BUTTON_HEIGHT) / 2;
    TextButton textButton = new TextButton(x, y, I18NUtil.getString("lobby.welcome"));
    BufferedImage background = ImageUtil.loadImage(ImageConstant.WELCOME_BACKGROUND);

    return WelcomeState.builder()
        .gamePanel(gamePanel)
        .loginPanel(loginPanel)
        .option(option)
        .textButton(textButton)
        .background(background)
        .build();
  }

  private static CustomModalBorder createSetting() {
    return new CustomModalBorder(new SettingPanel(), "Setting", ImageConstant.SETTING_ICON);
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
