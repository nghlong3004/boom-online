package vn.nghlong3004.boom.online.server.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 1/8/2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleUserInfoResponse(
    String sub,
    String email,
    String name,
    @JsonProperty("given_name") String givenName,
    @JsonProperty("family_name") String familyName,
    String picture,
    @JsonProperty("email_verified") Boolean emailVerified,
    String locale) {}
