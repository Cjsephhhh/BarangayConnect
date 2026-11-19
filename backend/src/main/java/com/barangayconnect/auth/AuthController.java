package com.barangayconnect.auth;
import jakarta.servlet.http.HttpServletRequest;import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import static com.barangayconnect.auth.AuthDtos.*;
@RestController @RequestMapping("/api/v1/auth") public class AuthController {
 private final AuthService service;public AuthController(AuthService s){service=s;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public AuthResponse register(@Valid @RequestBody RegisterRequest r,HttpServletRequest q){return service.register(r,q);}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r,HttpServletRequest q){return service.login(r,q);}
 @PostMapping("/logout") public MessageResponse logout(@RequestHeader(value="Authorization",required=false)String h,Authentication a,HttpServletRequest q){service.logout(h,a.getName(),q);return new MessageResponse("You have been logged out.");}
 @GetMapping("/me") public UserResponse me(Authentication a){return service.current(a.getName());}
}