package com.vn.nghlong3004.boomonline.server.service.impl;

import com.vn.nghlong3004.boomonline.server.constant.LocaleConstant;
import com.vn.nghlong3004.boomonline.server.service.EmailService;
import com.vn.nghlong3004.boomonline.server.service.email.EmailLocaleStrategy;
import com.vn.nghlong3004.boomonline.server.service.email.EmailType;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final Map<String, EmailLocaleStrategy> emailStrategies;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Override
  @Async
  public void sendHtmlEmail(String toEmail, String lang, EmailType type, Map<String, String> data) {
    try {
      EmailLocaleStrategy strategy =
          emailStrategies.getOrDefault(lang, emailStrategies.get(LocaleConstant.VIETNAMESE));

      String subject = strategy.getSubject(type);
      String templatePath = strategy.getTemplatePath(type);

      String htmlContent = readTemplate(templatePath);

      htmlContent = replacePlaceholders(htmlContent, data);

      sendMimeMessage(toEmail, subject, htmlContent);

    } catch (Exception e) {
      log.error("Error send mail: {}", e.getMessage());
    }
  }

  private String readTemplate(String templatePath) throws Exception {
    ClassPathResource resource = new ClassPathResource(templatePath);
    return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
  }

  private String replacePlaceholders(String htmlContent, Map<String, String> data) {
    if (data == null || data.isEmpty()) {
      return htmlContent;
    }

    for (Map.Entry<String, String> entry : data.entrySet()) {
      String key = "{{%s}}".formatted(entry.getKey());
      String value = entry.getValue();
      if (htmlContent.contains(key) && value != null) {
        htmlContent = htmlContent.replace(key, value);
      }
    }
    return htmlContent;
  }

  private void sendMimeMessage(String toEmail, String subject, String htmlContent)
      throws Exception {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

    helper.setFrom(fromEmail);
    helper.setTo(toEmail);
    helper.setSubject(subject);
    helper.setText(htmlContent, true);

    mailSender.send(mimeMessage);
  }
}
