package vn.nghlong3004.boom.online.server.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/17/2025
 */
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

  private static final int REQUEST_CACHE_LIMIT = 50 * 1024;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    ContentCachingRequestWrapper requestWrapper =
        new ContentCachingRequestWrapper(request, REQUEST_CACHE_LIMIT);
    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

    long startTime = System.currentTimeMillis();

    try {
      filterChain.doFilter(requestWrapper, responseWrapper);
    } finally {
      long timeTaken = System.currentTimeMillis() - startTime;
      String requestBody =
          getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
      String responseBody =
          getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
      log.info(
          """
                      LOGGING REQUEST/RESPONSE:
                      ----------------------------------------------------------------
                      METHOD         : {}
                      URL            : {}
                      REQUEST BODY   : {}
                      RESPONSE CODE  : {}
                      RESPONSE BODY  : {}
                      TIME TAKEN     : {} ms
                      ----------------------------------------------------------------
          """,
          request.getMethod(),
          request.getRequestURI(),
          requestBody,
          response.getStatus(),
          responseBody,
          timeTaken);
      responseWrapper.copyBodyToResponse();
    }
  }

  private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
    try {
      if (contentAsByteArray == null || contentAsByteArray.length == 0) {
        return "";
      }
      return new String(
          contentAsByteArray, characterEncoding != null ? characterEncoding : "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return "[Error reading content]";
    }
  }
}
