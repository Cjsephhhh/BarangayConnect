package com.barangayconnect.auth;

import static com.barangayconnect.auth.AuthDtos.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(
      @Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
    return service.register(request, httpRequest);
  }

  @PostMapping("/login")
  public AuthResponse login(
      @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
    return service.login(request, httpRequest);
  }

  @PostMapping("/logout")
  public MessageResponse logout(
      @RequestHeader(value = "Authorization", required = false) String header,
      Authentication authentication,
      HttpServletRequest httpRequest) {
    service.logout(header, authentication.getName(), httpRequest);
    return new MessageResponse("You have been logged out.");
  }

  @GetMapping("/me")
  public UserResponse me(Authentication authentication) {
    return service.current(authentication.getName());
  }

  public record ProfileRequest(
      @NotBlank @Size(max = 120) String fullName,
      @NotBlank @Size(max = 220) String address,
      @NotBlank @jakarta.validation.constraints.Pattern(regexp = "^09[0-9]{9}$")
          String mobileNumber) {}

  @PutMapping("/profile")
  public UserResponse profile(
      @Valid @RequestBody ProfileRequest request,
      Authentication authentication,
      HttpServletRequest httpRequest) {
    return service.updateProfile(
        authentication.getName(),
        request.fullName(),
        request.address(),
        request.mobileNumber(),
        httpRequest);
  }

  public record PasswordRequest(
      @NotBlank String currentPassword,
      @NotBlank @Size(min = 8, max = 72) String newPassword,
      @NotBlank String confirmPassword) {}

  @PutMapping("/password")
  public MessageResponse password(
      @Valid @RequestBody PasswordRequest request, Authentication authentication) {
    if (!request.newPassword().equals(request.confirmPassword())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Password confirmation does not match.");
    }
    service.changePassword(
        authentication.getName(), request.currentPassword(), request.newPassword());
    return new MessageResponse("Password changed. Please sign in again with your new password.");
  }
}
