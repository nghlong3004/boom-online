package com.vn.nghlong3004.boomonline.server.service;

import com.vn.nghlong3004.boomonline.server.model.request.RegisterRequest;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public interface AuthService {
  void register(RegisterRequest request);
}
