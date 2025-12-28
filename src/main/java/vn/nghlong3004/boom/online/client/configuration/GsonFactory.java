package vn.nghlong3004.boom.online.client.configuration;

import com.google.gson.*;
import java.time.Instant;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/26/2025
 */
public class GsonFactory {
  public static Gson createGson() {
    return new GsonBuilder()
        .registerTypeAdapter(
            Instant.class,
            (JsonDeserializer<Instant>)
                (json, typeOfT, context) -> Instant.parse(json.getAsString()))
        .registerTypeAdapter(
            Instant.class,
            (JsonSerializer<Instant>)
                (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
        .create();
  }
}
