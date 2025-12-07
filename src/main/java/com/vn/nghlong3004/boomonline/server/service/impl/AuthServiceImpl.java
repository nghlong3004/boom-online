package com.vn.nghlong3004.boomonline.server.service.impl;

import com.vn.nghlong3004.boomonline.server.exception.ErrorCode;
import com.vn.nghlong3004.boomonline.server.exception.ResourceException;
import com.vn.nghlong3004.boomonline.server.mapper.UserMapper;
import com.vn.nghlong3004.boomonline.server.model.User;
import com.vn.nghlong3004.boomonline.server.model.request.RegisterRequest;
import com.vn.nghlong3004.boomonline.server.repository.UserRepository;
import com.vn.nghlong3004.boomonline.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Project: boom-online-server
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public void register(RegisterRequest request) {
    log.info("Processing registration request for email: {}", request.email());
    if (userRepository.existsByEmail(request.email())) {
      log.warn("Registration failed. Email {} already exists.", request.email());
      throw new ResourceException(ErrorCode.EMAIL_ALREADY);
    }
    User user = userMapper.toEntity(request);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    User savedUser = userRepository.save(user);
    log.info("User registered successfully with ID: {}", savedUser.getId());
  }
}
