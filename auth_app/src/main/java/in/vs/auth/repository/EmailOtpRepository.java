package in.vs.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import in.vs.auth.entities.EmailOtp;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, UUID>{
	
	Optional<EmailOtp> findTopByEmailOrderByExpiryTimeDesc(String email);
}
