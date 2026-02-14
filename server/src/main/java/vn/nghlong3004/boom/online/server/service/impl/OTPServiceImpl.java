package vn.nghlong3004.boom.online.server.service.impl;

import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.nghlong3004.boom.online.server.exception.ErrorCode;
import vn.nghlong3004.boom.online.server.exception.ResourceException;
import vn.nghlong3004.boom.online.server.model.ExchangeToken;
import vn.nghlong3004.boom.online.server.model.OTP;
import vn.nghlong3004.boom.online.server.repository.ExchangeTokenRepository;
import vn.nghlong3004.boom.online.server.repository.OTPRepository;
import vn.nghlong3004.boom.online.server.service.OTPService;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

  private static final String OTP_FORMAT = "%08d";
  private static final int OTP_BOUND = 100_000_000;

  private final ExchangeTokenRepository tokenRepository;
  private final OTPRepository otpRepository;
  private final SecureRandom secureRandom;

  @Value("${application.otp.expiry.minutes}")
  private int otpExpiryMinutes;

  @Value("${application.token.expiry.minutes}")
  private int tokenExpiryMinutes;

  @Override
  @Transactional
  public String createOTP(String email) {
    log.info("Start processing for email: {}", email);

    OTP otp = otpRepository.findOTPByEmail(email).orElse(new OTP());

    String code = String.format(OTP_FORMAT, secureRandom.nextInt(OTP_BOUND));

    Instant expiryTime = Instant.now().plus(Duration.ofMinutes(otpExpiryMinutes));

    otp.setEmail(email);
    otp.setCode(code);
    otp.setExpiryTime(expiryTime);

    otpRepository.save(otp);

    log.info("Success generated & saved for email: {}", email);
    return code;
  }

  @Override
  @Transactional
  public void verifyOTP(String email, String otpInput) {
    log.info("Verifying for email: {}", email);
    if (otpInput == null || otpInput.trim().isEmpty()) {
      throw new ResourceException(ErrorCode.OTP_INCORRECT);
    }
    OTP otp =
        otpRepository
            .findOTPByEmail(email)
            .orElseThrow(() -> new ResourceException(ErrorCode.OTP_NOT_FOUND));

    if (Instant.now().isAfter(otp.getExpiryTime())) {
      otpRepository.delete(otp);
      throw new ResourceException(ErrorCode.OTP_EXPIRED);
    }

    if (!otp.getCode().equals(otpInput)) {
      throw new ResourceException(ErrorCode.OTP_INCORRECT);
    }

    otpRepository.delete(otp);
    log.info("Success verified for email: {}", email);
  }

  @Override
  public String createExchangeToken(String email) {
    String tokenString = UUID.randomUUID().toString();

    ExchangeToken tokenEntity =
        ExchangeToken.builder()
            .token(tokenString)
            .email(email)
            .expiryTime(Instant.now().plus(Duration.ofMinutes(tokenExpiryMinutes)))
            .build();

    tokenRepository.save(tokenEntity);

    log.info("Generated exchange token for email: {}", email);
    return tokenString;
  }

  @Override
  @Transactional
  public void verifyExchangeToken(String tokenInput) {
    log.info("Verifying exchange token: {}", tokenInput);

    ExchangeToken tokenEntity =
        tokenRepository
            .findByToken(tokenInput)
            .orElseThrow(() -> new ResourceException(ErrorCode.TOKEN_INCORRECT));

    if (Instant.now().isAfter(tokenEntity.getExpiryTime())) {
      tokenRepository.delete(tokenEntity);
      throw new ResourceException(ErrorCode.TOKEN_EXPIRED);
    }

    tokenRepository.delete(tokenEntity);

    log.info("Success verified exchange token for email: {}", tokenEntity.getEmail());
  }
}
