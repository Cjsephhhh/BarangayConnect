package com.barangayconnect.auth;

import com.barangayconnect.auth.AuthDtos.MessageResponse;
import com.barangayconnect.auth.AuthDtos.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
  private final AuthService authService;
  private final PasswordResetService resetService;

  public ProfileController(AuthService authService, PasswordResetService resetService) {
    this.authService = authService;
    this.resetService = resetService;
  }

  public record UpdateProfileRequest(
      @NotBlank @Size(max = 120) String fullName,
      @NotBlank @Size(max = 220) String address,
      @NotBlank
          @Pattern(
              regexp = "^09[0-9]{9}$",
              message = "Use a valid Philippine mobile number (09XXXXXXXXX).")
          String mobileNumber) {}

  public record ChangePasswordRequest(
      @NotBlank String currentPassword,
      @NotBlank @Size(min = 8, max = 72) String newPassword,
      @NotBlank String confirmPassword) {}

  public record ForgotPasswordRequest(
      @NotBlank @jakarta.validation.constraints.Email String email) {}

  public record ResetPasswordRequest(
      @NotBlank String token,
      @NotBlank @Size(min = 8, max = 72) String newPassword,
      @NotBlank String confirmPassword) {}

  @GetMapping
  public UserResponse getProfile(Authentication authentication) {
    return authService.current(authentication.getName());
  }

  @PutMapping
  public UserResponse updateProfile(
      @Valid @RequestBody UpdateProfileRequest request,
      Authentication authentication,
      HttpServletRequest httpRequest) {
    return authService.updateProfile(
        authentication.getName(),
        request.fullName(),
        request.address(),
        request.mobileNumber(),
        httpRequest);
  }

  @PutMapping("/password")
  public MessageResponse changePassword(
      @Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
    if (!request.newPassword().equals(request.confirmPassword())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Password confirmation does not match.");
    }
    authService.changePassword(
        authentication.getName(), request.currentPassword(), request.newPassword());
    return new MessageResponse("Password changed. Please sign in again with your new password.");
  }

  @PostMapping("/forgot-password")
  public MessageResponse forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {
    resetService.requestReset(request.email(), httpRequest);
    return new MessageResponse(
        "If an account exists for that email, a password reset link has been sent.");
  }

  @PostMapping("/reset-password")
  public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    resetService.resetPassword(request.token(), request.newPassword(), request.confirmPassword());
    return new MessageResponse(
        "Password reset successfully. You can now sign in with your new password.");
  }
}
