package vn.nghlong3004.boom.online.client.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Slf4j
public class FileLoader {
  private FileLoader() {}

  public static String loadFile(String filePath) {
    try (InputStream inputStream = FileLoader.class.getResourceAsStream(filePath)) {
      if (inputStream == null) {
        throw new RuntimeException("File: {%s} not found!".formatted(filePath));
      }
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
