package in.vs.auth.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import in.vs.auth.entities.Provider;
import in.vs.auth.entities.User;
import in.vs.auth.repository.UserRepository;
import in.vs.auth.service.AuthTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

//    private final AuthenticationManager authenticationManager;

	private final UserRepository userRepository;
//	private final JwtService jwtService;
//	private final CookieService cookieService;
	private final AuthTokenService authTokenService;
//	private final RefreshTokenRepository refreshTokenRepository;
	
	@Value("${app.auth.frontend.success-redirect}")
	private String frontEndSuccessUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

		String registrationId = "unknown";
		if (authentication instanceof OAuth2AuthenticationToken token) {
			registrationId = token.getAuthorizedClientRegistrationId();
		}

		User user;
		switch (registrationId) {
		case "google" -> {
			String googleId = oAuth2User.getAttributes().getOrDefault("sub", "").toString();
			String email = oAuth2User.getAttributes().getOrDefault("email", "").toString();
			String name = oAuth2User.getAttributes().getOrDefault("name", "").toString();
			String picture = oAuth2User.getAttributes().getOrDefault("picture", "").toString();
			User user1 = User.builder().email(email).name(name).image(picture).enabel(true).provider(Provider.GOOGLE).providerId(googleId).build();
//			userRepository.findByEmail(email).ifPresentOrElse(user1 -> {
//				System.out.print("User Already exist");
//			}, () -> {
//				userRepository.save(user);
//			});
			user = userRepository.findByEmail(email)
			        .orElseGet(() -> userRepository.save(user1));
		}
		case "github" -> {
			String name = oAuth2User.getAttributes().getOrDefault("login", "").toString();
			String githubId = oAuth2User.getAttributes().getOrDefault("id", "").toString();
			String image = oAuth2User.getAttributes().getOrDefault("avatar_url", "").toString();
			
			String email = (String)oAuth2User.getAttributes().get("email");
			if(email == null || email.isBlank()) {
				email = name + "@github.com";
			}
			
			User newUser = User.builder()
					.email(email)
					.name(name)
					.image(image)
					.enabel(true)
					.provider(Provider.GITHUB)
					.providerId(githubId)
					.build();
			
			user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));
		}
		default -> {
			throw new RuntimeException("Invalid Registration Id");
		}
		}

//		String jti = UUID.randomUUID().toString();
//		RefreshToken refreshTokenOb = RefreshToken.builder().jti(jti).user(user).revoked(false).createdAt(Instant.now())
//				.expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds())).build();
//
//		String accessToken = jwtService.generateAccessToken(user);
//		String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());
//		cookieService.attachRefreshCookie(response, refreshToken, (int)jwtService.getRefreshTtlSeconds());
		
//		authTokenService.createAuthTokens(user, response);
		
//		response.getWriter().write("login Successfull");
//		response.sendRedirect("/api/v1/users");
//		response.sendRedirect(frontEndSuccessUrl);
		
		Map<String, String> tokens =
		        authTokenService.createAuthTokens(user, response);

		String accessToken = tokens.get("accessToken");

		response.sendRedirect(
			    frontEndSuccessUrl
			    + "?accessToken=" + accessToken
			    + "&email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
			    + "&name=" + URLEncoder.encode(user.getName(), StandardCharsets.UTF_8)
			    + "&image=" + URLEncoder.encode(
			          user.getImage() == null ? "" : user.getImage(),
			          StandardCharsets.UTF_8
			      )
			);
	}

}
