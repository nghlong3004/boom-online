package vn.nghlong3004.boom.online.server.service;

import vn.nghlong3004.boom.online.server.model.AuthenticatedUser;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/25/2025
 */
public interface UserService {
  AuthenticatedUser getCurrentUser();
}
