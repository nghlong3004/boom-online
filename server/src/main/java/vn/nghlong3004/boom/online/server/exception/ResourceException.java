package vn.nghlong3004.boom.online.server.exception;

import lombok.Getter;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Getter
public class ResourceException extends RuntimeException {

  private final ErrorCode errorCode;

  public ResourceException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
