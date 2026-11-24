package com.barangayconnect.auth;

import com.barangayconnect.user.UserAccount;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;
  private final long expirationSeconds;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-minutes:30}") long minutes) {
    if (secret.getBytes(StandardCharsets.UTF_8).length < 32)
      throw new IllegalStateException("JWT_SECRET must be at least 32 bytes.");
    key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    expirationSeconds = minutes * 60;
  }

  public String createToken(UserAccount u, String jti) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(u.getEmail())
        .id(jti)
        .claim("role", u.getRole().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(expirationSeconds)))
        .signWith(key)
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }

  public String newJti() {
    return UUID.randomUUID().toString();
  }

  public long getExpirationSeconds() {
    return expirationSeconds;
  }
}
