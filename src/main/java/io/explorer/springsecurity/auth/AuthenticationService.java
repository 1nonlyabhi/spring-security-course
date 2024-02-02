package io.explorer.springsecurity.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.explorer.springsecurity.config.JwtService;
import io.explorer.springsecurity.token.Token;
import io.explorer.springsecurity.token.TokenRepository;
import io.explorer.springsecurity.token.TokenType;
import io.explorer.springsecurity.user.Role;
import io.explorer.springsecurity.user.User;
import io.explorer.springsecurity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtService jwtService;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private TokenRepository tokenRepository;

  public AuthenticationResponse register(RegisterRequest request) {
    var user =
        User.builder()
            .email(request.getEmail())
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
    User savedUser = userRepository.save(user);
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, accessToken);
    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticateRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, accessToken);
    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  private void revokeAllUserTokens(User user) {
    List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
    if (validTokens.isEmpty()) {
      return;
    }
    validTokens.forEach(
        token -> {
          token.setRevoked(true);
          token.setExpired(true);
        });
  }

  private void saveUserToken(User user, String jwtToken) {
    Token token =
        Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    final String refreshToken = authHeader.substring(7);
    final String userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      User user = userRepository.findByEmail(userEmail).orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        String accessToken = jwtService.generateAccessToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse =
            AuthenticationResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
