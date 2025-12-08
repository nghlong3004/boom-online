package com.vn.nghlong3004.boomonline.server.service.impl;

import com.vn.nghlong3004.boomonline.server.exception.ErrorCode;
import com.vn.nghlong3004.boomonline.server.exception.ResourceException;
import com.vn.nghlong3004.boomonline.server.mapper.UserMapper;
import com.vn.nghlong3004.boomonline.server.model.Role;
import com.vn.nghlong3004.boomonline.server.model.User;
import com.vn.nghlong3004.boomonline.server.model.request.LoginRequest;
import com.vn.nghlong3004.boomonline.server.model.request.RegisterRequest;
import com.vn.nghlong3004.boomonline.server.model.response.LoginResponse;
import com.vn.nghlong3004.boomonline.server.repository.UserRepository;
import com.vn.nghlong3004.boomonline.server.service.AuthService;
import com.vn.nghlong3004.boomonline.server.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final TokenService tokenService;
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
    user.setRole(Role.USER);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    User savedUser = userRepository.save(user);
    log.info("User registered successfully with ID: {}", savedUser.getId());
  }

  @Override
  public LoginResponse login(LoginRequest request) {

    Authentication authentication;
    try {
      authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    } catch (BadCredentialsException | UsernameNotFoundException e) {
      throw new ResourceException(ErrorCode.INVALID_CREDENTIALS);
    }

    String accessToken = tokenService.generateAccessToken(authentication);
    String refreshToken = tokenService.generateRefreshToken(authentication);

    return new LoginResponse(accessToken, refreshToken);
  }
}
