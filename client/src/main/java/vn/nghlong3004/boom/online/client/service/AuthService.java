package vn.nghlong3004.boom.online.client.service;

import java.util.function.Consumer;
import vn.nghlong3004.boom.online.client.model.response.LoginResponse;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
public interface AuthService {

  void loginWithGoogle(Consumer<LoginResponse> onSuccess, Consumer<String> onError);

  void cancelGoogleLogin();

  boolean isGoogleLoginInProgress();
}
