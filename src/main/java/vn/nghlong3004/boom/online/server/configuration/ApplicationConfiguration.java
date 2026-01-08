package vn.nghlong3004.boom.online.server.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import vn.nghlong3004.boom.online.server.email.LocaleStrategy;
import vn.nghlong3004.boom.online.server.model.GameState;

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
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public Map<String, ScheduledFuture<?>> scheduledTasks() {
    return new ConcurrentHashMap<>();
  }

  @Bean
  public Map<String, GameState> games() {
    return new ConcurrentHashMap<>();
  }

  @Bean
  public Map<String, LocaleStrategy> emailStrategies(List<LocaleStrategy> strategies) {
    return strategies.stream()
        .collect(Collectors.toMap(LocaleStrategy::getSupportedLanguage, Function.identity()));
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
