package in.vs.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;
	
	public void sendOtpEmail(String to, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setTo(to);
		
		message.setSubject("Your Otp for Auth App");
		
		message.setText("Your Otp: " + otp);
		
		mailSender.send(message);
	}
}
