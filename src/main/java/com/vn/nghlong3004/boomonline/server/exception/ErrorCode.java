package com.vn.nghlong3004.boomonline.server.exception;

import com.vn.nghlong3004.boomonline.server.model.response.ErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // 4xx
  NOT_FOUND(HttpStatus.NOT_FOUND.value(), "NotFoundError", "Not Found"),
  ENDPOINT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "EndpointNotFoundError", "Endpoint Not Found"),
  EMAIL_ALREADY(HttpStatus.CONFLICT.value(), "EmailAlready", "Email already exists."),
  INVALID_CREDENTIALS(
      HttpStatus.UNAUTHORIZED.value(), "InvalidCredentials", "Email or password is incorrect."),
  EMAIL_INCORRECT(HttpStatus.UNAUTHORIZED.value(), "EmailIncorrect", "Email is incorrect."),

  FORBIDDEN(HttpStatus.FORBIDDEN.value(), "Forbidden", "Access denied."),

  INVALID_REQUEST(
      HttpStatus.BAD_REQUEST.value(), "InvalidRequest", "The request parameters are invalid."),

  INVALID_ACCESS_TOKEN(
      HttpStatus.UNAUTHORIZED.value(), "InvalidAccessToken", "Access token is invalid."),

  ACCESS_TOKEN_EXPIRED(
      HttpStatus.UNAUTHORIZED.value(), "AccessTokenExpired", "Access token has expired."),

  INVALID_REFRESH_TOKEN(
      HttpStatus.UNAUTHORIZED.value(), "InvalidRefreshToken", "Refresh token is invalid."),

  REFRESH_TOKEN_EXPIRED(
      HttpStatus.UNAUTHORIZED.value(), "RefreshTokenExpired", "Refresh token has expired."),

  OTP_NOT_FOUND(
      HttpStatus.NOT_FOUND.value(), "OtpNotFound", "OTP request not found or has expired."),

  OTP_EXPIRED(HttpStatus.BAD_REQUEST.value(), "OtpExpired", "OTP code has expired."),

  OTP_INCORRECT(HttpStatus.BAD_REQUEST.value(), "OtpIncorrect", "OTP code is incorrect."),

  TOKEN_INCORRECT(HttpStatus.BAD_REQUEST.value(), "TokenIncorrect", "Token is incorrect."),

  // 5xx
  INTERNAL_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      "InternalError",
      "The server encountered an internal error or misconfiguration and was unable to complete your request.");

  private final int status;
  private final String code;
  private final String message;

  public ErrorResponse toErrorResponse() {
    return new ErrorResponse(message, status, code);
  }
}
