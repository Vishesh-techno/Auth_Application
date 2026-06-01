package in.vs.auth.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import in.vs.auth.security.JwtAuthenticationFilter;
import in.vs.auth.security.OAuth2SuccessHandler;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final OAuth2SuccessHandler successHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http.csrf(e -> e.disable()).cors(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/error", "/oauth2/**", "/login/**",
						"/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/logout",
						"/api/v1/otp/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll().anyRequest().authenticated())
				.oauth2Login(oauth -> oauth.successHandler(successHandler)).logout(AbstractHttpConfigurer::disable)
				.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, e) -> {
					e.printStackTrace();
					response.setStatus(401);
					response.setContentType("application/json");
					String message = "Unauthorized Access " + e.getMessage();
					Map<String, String> errorMap = Map.of("message", message, "status", String.valueOf(401),
							"statusCode", Integer.toString(401));
					var objectMapper = new ObjectMapper();
					response.getWriter().write(objectMapper.writeValueAsString(errorMap));
				})).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
		return configuration.getAuthenticationManager();
	}

//	@Bean
//	public UserDetailsService users() {
//		User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();
//		UserDetails user1 = userBuilder.username("user").password("abc").roles("ADMIN").build();
//		UserDetails user2 = userBuilder.username("jatin").password("xyz").roles("ADMIN").build();
//		UserDetails user3 = userBuilder.username("vishesh").password("pqr").roles("USER").build();
//		return new InMemoryUserDetailsManager(user1, user2, user3);
//	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource(
	        @Value("${app.cors.front-end-url}") String corsUrl) {

	    String[] urls = corsUrl.trim().split(",");

	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(Arrays.asList(urls));
	    config.setAllowedMethods(List.of(
	            "GET", "POST", "PUT", "DELETE",
	            "PATCH", "OPTIONS", "HEAD"));

	    config.setAllowedHeaders(List.of("*"));
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source =
	            new UrlBasedCorsConfigurationSource();

	    source.registerCorsConfiguration("/**", config);

	    return source;
	}
}
