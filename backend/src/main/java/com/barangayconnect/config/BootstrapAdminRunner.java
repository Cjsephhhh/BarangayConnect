package com.barangayconnect.config;

import com.barangayconnect.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapAdminRunner implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    @Value("${app.bootstrap-admin.email:}") private String email;
    @Value("${app.bootstrap-admin.password:}") private String password;
    @Value("${app.bootstrap-admin.full-name:System Administrator}") private String fullName;
    @Value("${app.bootstrap-admin.mobile:09999999999}") private String mobile;
    @Value("${app.bootstrap-admin.address:BarangayConnect Administration}") private String address;

    public BootstrapAdminRunner(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) return;
        String normalized = email.trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(normalized)) return;
        UserAccount admin = new UserAccount();
        admin.setFullName(fullName);
        admin.setEmail(normalized);
        admin.setMobileNumber(mobile);
        admin.setAddress(address);
        admin.setPasswordHash(encoder.encode(password));
        admin.setRole(UserRole.SYSTEM_ADMINISTRATOR);
        users.save(admin);
    }
}