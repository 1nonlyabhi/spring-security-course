package io.explorer.springsecurity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

  private String currentPassword;
  private String newPassword;
  private String confirmationPassword;
}
