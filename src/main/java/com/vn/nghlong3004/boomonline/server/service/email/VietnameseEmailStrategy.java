package com.vn.nghlong3004.boomonline.server.service.email;

import com.vn.nghlong3004.boomonline.server.constant.LocaleConstant;
import org.springframework.stereotype.Service;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
@Service
public class VietnameseEmailStrategy implements EmailLocaleStrategy {

  @Override
  public String getSupportedLanguage() {
    return LocaleConstant.VIETNAMESE;
  }

  @Override
  public String getSubject(EmailType type) {
    return switch (type) {
      case OTP -> LocaleConstant.OTP_SUBJECT_VI;
      case WELCOME -> LocaleConstant.WELCOME_SUBJECT_VI;
      case RESET_SUCCESS -> LocaleConstant.RESET_SUCCESS_SUBJECT_VI;
    };
  }

  @Override
  public String getTemplatePath(EmailType type) {
    return switch (type) {
      case OTP -> LocaleConstant.OTP_TEMPLATE_VI;
      case WELCOME -> LocaleConstant.WELCOME_TEMPLATE_VI;
      case RESET_SUCCESS -> LocaleConstant.RESET_SUCCESS_TEMPLATE_VI;
    };
  }
}
