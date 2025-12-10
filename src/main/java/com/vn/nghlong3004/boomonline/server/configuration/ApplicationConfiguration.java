package com.vn.nghlong3004.boomonline.server.configuration;

import com.vn.nghlong3004.boomonline.server.service.email.EmailLocaleStrategy;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Configuration
public class ApplicationConfiguration {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecureRandom secureRandom() {
    return new SecureRandom();
  }

  @Bean
  public Map<String, EmailLocaleStrategy> emailStrategies(List<EmailLocaleStrategy> strategies) {
    return strategies.stream()
        .collect(Collectors.toMap(EmailLocaleStrategy::getSupportedLanguage, Function.identity()));
  }

  @Bean
  public Map<String, String> verificationTokens() {
    return new ConcurrentHashMap<>();
  }
}
