package com.barangayconnect.admin;

import com.barangayconnect.audit.AuditLog;
import com.barangayconnect.auth.*;
import com.barangayconnect.auth.AuthDtos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
public class AdminController {
  private final AuthService service;

  public AdminController(AuthService s) {
    service = s;
  }

  @GetMapping("/users")
  public List<UserResponse> users() {
    return service.all();
  }

  @PostMapping("/officials")
  public UserResponse create(@Valid @RequestBody CreateOfficialRequest r, HttpServletRequest q) {
    return service.createOfficial(r, q);
  }

  public record StatusRequest(boolean active) {}

  @PatchMapping("/users/{id}/status")
  public MessageResponse status(
      @PathVariable Long id, @RequestBody StatusRequest r, Authentication a) {
    service.setActive(id, r.active(), a.getName());
    return new MessageResponse("Account status updated.");
  }

  @GetMapping("/audit-logs")
  public List<AuditLog> logs() {
    return service.logs();
  }
}
