package com.barangayconnect.auth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface LoginSessionRepository extends JpaRepository<LoginSession,Long>{Optional<LoginSession> findByJti(String jti);}