package in.vs.auth.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.vs.auth.dto.LoginRequest;
import in.vs.auth.dto.RefreshTokenRequest;
import in.vs.auth.dto.TokenResponse;
import in.vs.auth.dto.UserDto;
import in.vs.auth.entities.RefreshToken;
import in.vs.auth.entities.User;
import in.vs.auth.repository.RefreshTokenRepository;
import in.vs.auth.repository.UserRepository;
import in.vs.auth.security.CookieService;
import in.vs.auth.security.JwtService;
import in.vs.auth.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final ModelMapper mapper;
	private final RefreshTokenRepository refreshTokenRepository;
	private final CookieService cookieService;

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

//		authenticate user
		authenticate(loginRequest);
		User user = userRepository.findByEmail(loginRequest.email())
				.orElseThrow(() -> new BadCredentialsException("Email is not Valid or not registered"));
		if (!user.isEnabel()) {
			throw new DisabledException("User is Disabled");
		}

//		Refresh Token Generate
		String jti = UUID.randomUUID().toString();
		var refreshTokenObj = RefreshToken.builder().jti(jti).user(user).createdAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds())).revoked(false).build();

		refreshTokenRepository.save(refreshTokenObj);

//		Access generate token here 
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user, refreshTokenObj.getJti());

//		use cookie service to attach refresh token in cookie service
		cookieService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlSeconds());
		cookieService.addNoStoreHeaders(response);

		TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTtlSeconds(),
				mapper.map(user, UserDto.class));

		return ResponseEntity.ok(tokenResponse);
	}

	private Authentication authenticate(LoginRequest loginRequest) {
		try {
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid Username or Password");
		}
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refreshToken(@RequestBody(required = false) RefreshTokenRequest body,
			HttpServletRequest request, HttpServletResponse response) {

		String refreshToken = readRefreshToken(body, request)
				.orElseThrow(() -> new BadCredentialsException("Refresh Token is missing"));
		
		if(!jwtService.isRefreshToken(refreshToken)) {
			throw new BadCredentialsException("Invalid Refresh Token");
		}
		
		String jti = jwtService.getJti(refreshToken);
		UUID userId = jwtService.getUserId(refreshToken);
		RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti)
		         .orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));
		
		if(storedRefreshToken.isRevoked()) {
			throw new BadCredentialsException("Refresh Token is Expired");
		}
		
		if(storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
			throw new BadCredentialsException("Refresh Token is Expired");
		}
		
		if(!storedRefreshToken.getUser().getId().equals(userId)) {
			throw new BadCredentialsException("Refresh Token does not belongs to this user");
		}
		
//		refresh token rotate
		storedRefreshToken.setRevoked(true);
		String newJti = UUID.randomUUID().toString();
		storedRefreshToken.setReplacedByToken(newJti);
		refreshTokenRepository.save(storedRefreshToken);
		
		User user = storedRefreshToken.getUser();
		
		var newRefreshTokenOb = RefreshToken.builder()
				.jti(newJti)
				.user(user)
				.createdAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
				.revoked(false)
				.build();

		refreshTokenRepository.save(newRefreshTokenOb);
		String newAccessToken = jwtService.generateAccessToken(user);
		String newRefreshToken = jwtService.generateRefreshToken(user, newRefreshTokenOb.getJti());
		
		cookieService.attachRefreshCookie(response, newRefreshToken, (int)jwtService.getRefreshTtlSeconds());
		cookieService.addNoStoreHeaders(response);
		return ResponseEntity.ok(TokenResponse.of(newAccessToken, newRefreshToken, jwtService.getAccessTtlSeconds(), mapper.map(user, UserDto.class)));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response){
		readRefreshToken(null, request)
		.ifPresent(token -> {
			try {
				if(jwtService.isRefreshToken(token)) {
					String jti = jwtService.getJti(token);
					refreshTokenRepository.findByJti(jti).ifPresent(rt -> {
						rt.setRevoked(true);
						refreshTokenRepository.save(rt);
					});
				}
				
			} catch (JwtException ignored) {
				// TODO: handle exception
			}
		});
		
		cookieService.clearRefreshCookie(response);
		cookieService.addNoStoreHeaders(response);
		SecurityContextHolder.clearContext();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	private Optional<String> readRefreshToken(RefreshTokenRequest body, HttpServletRequest request) {
//		1. read refresh token from cookie
		if (request.getCookies() != null) {
			Optional<String> fromCookie = Arrays.stream(request.getCookies())
					.filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
					.map(c -> c.getValue())
					.filter(v -> !v.isBlank())
					.findFirst();
			
			if(fromCookie.isPresent()) {
				return fromCookie;
			}
		}
		
		if(body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
			return Optional.of(body.refreshToken());
		}
		
		String refreshHeader = request.getHeader("X-Refresh-Token");
		if(refreshHeader != null && !refreshHeader.isBlank()) {
			return Optional.of(refreshHeader.trim());
		}
	  return Optional.empty();
	}

	@PostMapping("/register")
	public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
	}
}
