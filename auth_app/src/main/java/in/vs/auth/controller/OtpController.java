package in.vs.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.vs.auth.dto.LoginResponse;
import in.vs.auth.dto.OtpRequestDtos;
import in.vs.auth.dto.VerifyOtpDto;
import in.vs.auth.service.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {

	private final OtpService otpService;

	@PostMapping("/send")
	public String sendOtp(@RequestBody OtpRequestDtos dto) {
		otpService.sendOtp(dto.getEmail());
		return "Otp Sent Successfully";
	}

	@PostMapping("/verify")
	public LoginResponse verifyOtp(@RequestBody VerifyOtpDto dto, HttpServletResponse response) {
		return otpService.verifyOtp(dto.getEmail(), dto.getOtp(), response);
	}

}
