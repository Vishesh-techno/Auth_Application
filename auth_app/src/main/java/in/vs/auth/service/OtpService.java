package in.vs.auth.service;

import java.time.Instant;
import java.util.Random;

import org.springframework.stereotype.Service;

import in.vs.auth.dto.LoginResponse;
import in.vs.auth.entities.EmailOtp;
import in.vs.auth.entities.Provider;
import in.vs.auth.entities.User;
import in.vs.auth.exceptions.OtpException;
import in.vs.auth.repository.EmailOtpRepository;
import in.vs.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

	private final EmailOtpRepository otpRepository;
	private final EmailService emailService;
	private final UserRepository userRepository;
	private final AuthTokenService authTokenService;
	
	public void sendOtp(String email) {
		
	  String otp = String.valueOf(new Random().nextInt(900000) + 100000);
	
	  EmailOtp emailOtp = EmailOtp.builder()
			  .email(email)
			  .otp(otp)
			  .expiryTime(Instant.now().plusSeconds(300))
			  .verified(false)
			  .build();
	  
	  otpRepository.save(emailOtp);
	  
	  emailService.sendOtpEmail(email, otp);
	}

//	public boolean verifyOtp(String email, String otp) {
//		EmailOtp savedOtp = otpRepository
//				.findTopByEmailOrderByExpiryTimeDesc(email)
//				.orElseThrow();
//		
//		if(savedOtp.isVerified()) {
//			return false;
//		}
//		
//		if(savedOtp.getExpiryTime().isBefore(Instant.now())){
//			return false;
//		}
//		
//		savedOtp.setVerified(true);
//		otpRepository.save(savedOtp);
//		return true;
//	}

//	public Map<String, String> verifyOtp(String email, String otp, HttpServletResponse response){
//		EmailOtp savedOtp = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email)
//				.orElseThrow(() -> new RuntimeException("Otp Not Found"));
//		
//		if(savedOtp.isVerified()) {
//			throw new RuntimeException("Otp Already Used");
//		}
//		
//		if(savedOtp.getExpiryTime().isBefore(Instant.now())) {
//			throw new RuntimeException("Otp Expired");
//		}
//		
//		if(!savedOtp.getOtp().equals(otp)) {
//			throw new RuntimeException("Invalid Otp");
//		}
//		
//		savedOtp.setVerified(true);
//		otpRepository.save(savedOtp);
//		
//		User user = userRepository.findByEmail(email)
//				.orElseGet(() -> {
//					User newUser = User.builder()
//							.email(email)
//							.name(email)
//							.enabel(true)
//							.provider(Provider.LOCAL)
//							.build();
//					return userRepository.save(newUser);
//				});
//		return authTokenService.createAuthTokens(user, response);
//	}
	
	public LoginResponse verifyOtp(String email, String otp, HttpServletResponse response){
	    EmailOtp savedOtp = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email)
	            .orElseThrow(() -> new OtpException("OTP not found for this email"));
	    
	    if(savedOtp.isVerified()) {
	        throw new OtpException("OTP already used");
	    }
	    
	    if(savedOtp.getExpiryTime().isBefore(Instant.now())) {
	        throw new OtpException("OTP expired — please request a new one");
	    }
	    
	    if(!savedOtp.getOtp().equals(otp)) {
	        throw new OtpException("Invalid OTP — please check and try again");
	    }
	    
	    savedOtp.setVerified(true);
	    otpRepository.save(savedOtp);
	    
	    String name = email.contains("@")
	            ? email.substring(0, email.indexOf("@"))
	            : email;
	    
	    User user = userRepository.findByEmail(email)
	            .orElseGet(() -> {
	                User newUser = User.builder()
	                        .email(email)
	                        .name(name)
	                        .enabel(true)
	                        .provider(Provider.LOCAL)
	                        .build();
	                return userRepository.save(newUser);
	            });
	    return authTokenService.createAuthToken(user, response);
	}
}
