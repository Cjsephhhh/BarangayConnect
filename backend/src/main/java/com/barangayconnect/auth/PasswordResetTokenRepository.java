package com.barangayconnect.auth;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select t from PasswordResetToken t where t.tokenHash = :tokenHash and t.usedAt is null")
  Optional<PasswordResetToken> findUnusedTokenForUpdate(@Param("tokenHash") String tokenHash);

  void deleteByUser_IdAndUsedAtIsNull(Long userId);
}
