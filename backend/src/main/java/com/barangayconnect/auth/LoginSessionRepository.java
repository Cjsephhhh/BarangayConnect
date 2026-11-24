package com.barangayconnect.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface LoginSessionRepository extends JpaRepository<LoginSession, Long> {
    Optional<LoginSession> findByJti(String jti);

    @Modifying
    @Query("update LoginSession s set s.revoked = true where s.user.id = :userId and s.revoked = false")
    int revokeAllForUser(@Param("userId") Long userId);
}
