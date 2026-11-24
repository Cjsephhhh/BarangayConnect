package com.barangayconnect.auth;

import com.barangayconnect.audit.AuditLog;
import com.barangayconnect.audit.AuditLogRepository;
import com.barangayconnect.user.UserAccount;
import com.barangayconnect.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
public class PasswordResetService {
    private static final long TOKEN_LIFETIME_SECONDS = 900;
    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final LoginSessionRepository sessions;
    private final AuditLogRepository audit;
    private final ObjectProvider<JavaMailSender> mailSenders;
    private final String frontendUrl;
    private final String fromAddress;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserRepository users, PasswordResetTokenRepository tokens,
            LoginSessionRepository sessions, AuditLogRepository audit,
            ObjectProvider<JavaMailSender> mailSenders,
            @Value("${app.frontend-url}") String frontendUrl,
            @Value("${SMTP_FROM:no-reply@barangayconnect.local}") String fromAddress) {
        this.users = users;
        this.tokens = tokens;
        this.sessions = sessions;
        this.audit = audit;
        this.mailSenders = mailSenders;
        this.frontendUrl = frontendUrl;
        this.fromAddress = fromAddress;
    }

    @Transactional
    public void requestReset(String email, HttpServletRequest request) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        UserAccount user = users.findByEmailIgnoreCase(normalized).orElse(null);
        if (user == null) return;

        JavaMailSender sender = mailSenders.getIfAvailable();
        if (sender == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Password recovery email is not configured. Contact the system administrator.");
        }

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String tokenHash = hash(rawToken);
        tokens.deleteByUser_IdAndUsedAtIsNull(user.getId());
        tokens.save(new PasswordResetToken(user, tokenHash, Instant.now().plusSeconds(TOKEN_LIFETIME_SECONDS)));

        String link = frontendUrl.replaceAll("/+$", "") + "/reset-password?token=" + rawToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("BarangayConnect password reset");
        message.setText("Hello " + user.getFullName() + ",\n\n"
                + "We received a request to reset your BarangayConnect password. "
                + "Use the secure link below within 15 minutes. This link can only be used once.\n\n"
                + link + "\n\nIf you did not request this change, you can ignore this email. "
                + "Your current password will remain unchanged.\n\nBarangayConnect");
        sender.send(message);
        audit.save(new AuditLog(user.getId(), user.getEmail(), "PASSWORD_RESET_REQUESTED",
                "USER_ACCOUNT", user.getId(), request.getRemoteAddr()));
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword, String confirmPassword) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The reset link is invalid or expired.");
        }
        if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be between 8 and 72 characters.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password confirmation does not match.");
        }

        PasswordResetToken reset = tokens.findByTokenHashAndUsedAtIsNull(hash(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The reset link is invalid, expired, or already used."));
        if (reset.getExpiresAt().isBefore(Instant.now())) {
            reset.markUsed();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The reset link has expired. Request a new password reset email.");
        }

        UserAccount user = reset.getUser();
        user.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12)
                .encode(newPassword));
        reset.markUsed();
        sessions.revokeAllForUser(user.getId());
        audit.save(new AuditLog(user.getId(), user.getEmail(), "PASSWORD_RESET_COMPLETED",
                "USER_ACCOUNT", user.getId(), null));
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to process password reset token.", ex);
        }
    }
}
