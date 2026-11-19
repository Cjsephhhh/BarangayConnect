package com.barangayconnect.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<UserAccount,Long>{Optional<UserAccount> findByEmailIgnoreCase(String email);Optional<UserAccount> findByMobileNumber(String mobile);boolean existsByEmailIgnoreCase(String email);boolean existsByMobileNumber(String mobile);}