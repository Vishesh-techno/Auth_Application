package in.vs.auth.security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//import in.vs.auth.entities.User;
import in.vs.auth.helper.UserHelper;
import in.vs.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;
//	private final User user;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			if (!jwtService.isAccessToken(token)) {
				filterChain.doFilter(request, response);
				return;
			}
			try {
				Jws<Claims> parse = jwtService.parse(token);
				Claims payload = parse.getPayload();
				String userId = payload.getSubject();
				UUID userUuid = UserHelper.parseUUID(userId);

				userRepository.findById(userUuid).ifPresent(user -> {
					if (user.isEnabled()) {
						List<GrantedAuthority> authorities = user.getRoles() == null ? List.of()
								: user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
										.collect(Collectors.toList());

						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								user.getEmail(), null, authorities);

						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						if (SecurityContextHolder.getContext().getAuthentication() == null) {
							SecurityContextHolder.getContext().setAuthentication(authentication);
						}
					}
				});

			} catch (ExpiredJwtException e) {
				e.printStackTrace();
			} catch (MalformedJwtException e) {
				e.printStackTrace();
			} catch (JwtException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// TODO Auto-generated method stub
		return request.getRequestURI().startsWith("/api/v1/auth");
	}

	
}
