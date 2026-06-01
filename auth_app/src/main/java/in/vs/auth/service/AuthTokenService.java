package in.vs.auth.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import in.vs.auth.dto.LoginResponse;
import in.vs.auth.dto.UserDto;
import in.vs.auth.entities.RefreshToken;
import in.vs.auth.entities.User;
import in.vs.auth.repository.RefreshTokenRepository;
import in.vs.auth.security.CookieService;
import in.vs.auth.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final CookieService cookieService;

    private final RefreshTokenRepository refreshTokenRepository;
	
	private final JwtService jwtService;
	
	public Map<String, String> createAuthTokens(User user, HttpServletResponse response){
		String jti = UUID.randomUUID().toString();
		
		RefreshToken refreshTokenOb = RefreshToken.builder()
				.jti(jti)
				.user(user)
				.revoked(false)
				.createdAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
				.build();
		refreshTokenRepository.save(refreshTokenOb);
		
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());
		cookieService.attachRefreshCookie(response, refreshToken, (int)jwtService.getRefreshTtlSeconds());
		return Map.of("accessToken", accessToken);
	}
	
	public LoginResponse createAuthToken(User user, HttpServletResponse response) {

	    String jti = UUID.randomUUID().toString();

	    RefreshToken refreshTokenOb =
	            RefreshToken.builder()
	                    .jti(jti)
	                    .user(user)
	                    .revoked(false)
	                    .createdAt(Instant.now())
	                    .expiresAt(
	                        Instant.now().plusSeconds(
	                            jwtService.getRefreshTtlSeconds()
	                        )
	                    )
	                    .build();

	    refreshTokenRepository.save(refreshTokenOb);

	    String accessToken =
	            jwtService.generateAccessToken(user);

	    String refreshToken =
	            jwtService.generateRefreshToken(
	                    user,
	                    refreshTokenOb.getJti()
	            );

	    cookieService.attachRefreshCookie(
	            response,
	            refreshToken,
	            (int) jwtService.getRefreshTtlSeconds()
	    );

	    return LoginResponse.builder()
	            .accessToken(accessToken)
	            .user(
	                UserDto.builder()
	                    .id(user.getId())
	                    .name(user.getName())
	                    .email(user.getEmail())
	                    .createdAt(Instant.now())
	                    .updatedAt(Instant.now())
	                    .provider(user.getProvider())
	                    .build()
	            )
	            .build();
	}
}
