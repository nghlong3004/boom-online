package com.vn.nghlong3004.boomonline.server.service.impl;

import com.vn.nghlong3004.boomonline.server.exception.ErrorCode;
import com.vn.nghlong3004.boomonline.server.exception.ResourceException;
import com.vn.nghlong3004.boomonline.server.model.OTPInfo;
import com.vn.nghlong3004.boomonline.server.repository.OTPRepository;
import com.vn.nghlong3004.boomonline.server.service.OTPService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class OTPServiceImpl implements OTPService {

  private final OTPRepository otpRepository;
  private final SecureRandom secureRandom;
  private final Map<String, String> verificationTokens;

  @Value("${application.otp.expiry.minutes}")
  private int OTP_EXPIRY_MINUTES;

  @Override
  public String generateAndSaveOtp(String email) {
    log.info("Start processing for email: {}", email);
    otpRepository.deleteOtp(email);
    String otpCode = String.format("%08d", secureRandom.nextInt(100_000_000));
    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

    otpRepository.saveOtp(email, otpCode, expiryTime);
    log.info("Success generated & saved for email: {}", email);
    return otpCode;
  }

  @Override
  public void validateOtp(String email, String otpInput) {
    log.info("Verifying for email: {}", email);
    var otp = otpRepository.getOtp(email);

    if (otp.isEmpty()) {
      throw new ResourceException(ErrorCode.OTP_NOT_FOUND);
    }

    OTPInfo data = otp.get();

    if (LocalDateTime.now().isAfter(data.expiryTime())) {
      otpRepository.deleteOtp(email);
      throw new ResourceException(ErrorCode.OTP_EXPIRED);
    }

    if (!data.code().equals(otpInput)) {
      throw new ResourceException(ErrorCode.OTP_INCORRECT);
    }

    otpRepository.deleteOtp(email);
    log.info("Success verified for email: {}", email);
  }

  @Override
  public String generateExchangeToken(String email) {
    String token = UUID.randomUUID().toString();
    verificationTokens.put(token, email);
    return token;
  }

  @Override
  public void validateToken(String token) {
    log.info("Validating for token: {}", token);
    if (!verificationTokens.containsKey(token)) {
      throw new ResourceException(ErrorCode.TOKEN_INCORRECT);
    }
    verificationTokens.remove(token);
    log.info("Success validated for token: {}", token);
  }
}
