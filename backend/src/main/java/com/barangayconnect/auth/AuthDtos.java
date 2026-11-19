package com.barangayconnect.auth;
import com.barangayconnect.user.UserRole;
import jakarta.validation.constraints.*;
public final class AuthDtos {
 private AuthDtos(){}
 public record RegisterRequest(@NotBlank @Size(max=120) String fullName,@NotBlank @Size(max=220) String address,@NotBlank @Pattern(regexp="^09[0-9]{9}$",message="Use a valid Philippine mobile number (09XXXXXXXXX).") String mobileNumber,@NotBlank @Email @Size(max=180) String email,@NotBlank @Size(min=8,max=72) String password){}
 public record LoginRequest(@NotBlank String identifier,@NotBlank String password){}
 public record UserResponse(Long id,String fullName,String email,String mobileNumber,String address,UserRole role,boolean active){}
 public record AuthResponse(String token,String tokenType,long expiresInSeconds,UserResponse user){}
 public record MessageResponse(String message){}
 public record CreateOfficialRequest(@NotBlank @Size(max=120) String fullName,@NotBlank @Email String email,@NotBlank @Pattern(regexp="^09[0-9]{9}$") String mobileNumber,@NotBlank @Size(max=220) String address,@NotBlank @Size(min=8,max=72) String temporaryPassword,@NotNull UserRole role){}
}