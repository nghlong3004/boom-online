package com.vn.nghlong3004.boomonline.server.service;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
public interface OTPService {
  String generateAndSaveOtp(String email);

  void validateOtp(String email, String OTPInput);

  String generateExchangeToken(String email);

  void validateToken(String token);
}
