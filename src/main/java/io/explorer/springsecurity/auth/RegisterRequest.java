package io.explorer.springsecurity.auth;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  private String email;
  private String firstname;
  private String lastname;
  private String password;

  @Column(nullable = false)
  private String role;
}
