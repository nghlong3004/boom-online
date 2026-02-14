package vn.nghlong3004.boom.online.server.service;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/9/2025
 */
public interface OTPService {
  String createOTP(String email);

  void verifyOTP(String email, String OTPInput);

  String createExchangeToken(String email);

  void verifyExchangeToken(String token);
}
