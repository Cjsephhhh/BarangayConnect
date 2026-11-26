package com.barangayconnect.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByEmailIgnoreCase(String email);

  Optional<UserAccount> findByMobileNumber(String mobile);

  boolean existsByEmailIgnoreCase(String email);

  boolean existsByMobileNumber(String mobile);
}
