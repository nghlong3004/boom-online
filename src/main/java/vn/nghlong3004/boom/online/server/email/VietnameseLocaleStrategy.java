package vn.nghlong3004.boom.online.server.email;

import org.springframework.stereotype.Service;
import vn.nghlong3004.boom.online.server.constant.LocaleConstant;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
@Service
public class VietnameseLocaleStrategy implements LocaleStrategy {

  @Override
  public String getSupportedLanguage() {
    return LocaleConstant.VIETNAMESE;
  }

  @Override
  public String getSubject(TemplateType type) {
    return switch (type) {
      case OTP -> LocaleConstant.OTP_SUBJECT_VI;
      case WELCOME -> LocaleConstant.WELCOME_SUBJECT_VI;
      case RESET_SUCCESS -> LocaleConstant.RESET_SUCCESS_SUBJECT_VI;
      case LOGIN_OAUTH2 -> LocaleConstant.LOGIN_OAUTH2;
    };
  }

  @Override
  public String getTemplatePath(TemplateType type) {
    return switch (type) {
      case OTP -> LocaleConstant.OTP_TEMPLATE_VI;
      case WELCOME -> LocaleConstant.WELCOME_TEMPLATE_VI;
      case RESET_SUCCESS -> LocaleConstant.RESET_SUCCESS_TEMPLATE_VI;
      case LOGIN_OAUTH2 -> null;
    };
  }
}
