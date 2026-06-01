package in.vs.auth.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import in.vs.auth.entities.Role;
import in.vs.auth.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Service
@Data
public class JwtService {
	
	private final SecretKey key;
	private final long accessTtlSeconds;
	private final long refreshTtlSeconds;
	private final String issuer;
	
	public JwtService(
			    @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
	            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
	            @Value("${security.jwt.issuer}") String issuer,
	            @Value("${security.jwt.secret}") String secret) {
		
		if(secret == null || secret.length() < 32) {
			throw new IllegalArgumentException("Invalid Secret");
		}
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTtlSeconds = accessTtlSeconds;
		this.refreshTtlSeconds = refreshTtlSeconds;
		this.issuer = issuer;
	}
	
	
	public String generateAccessToken(User user) {
		Instant now = Instant.now();
		List<String> roles = user.getRoles() == null ? List.of() : 
			user.getRoles().stream().map(Role::getName).toList();
				return Jwts.builder()
						.id(UUID.randomUUID().toString())
						.subject(user.getId().toString())
						.issuer(issuer)
						.issuedAt(Date.from(now))
						.expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
						.claims(Map.of(
								"email", user.getEmail(),
								"roles", roles,
								"typ", "access"
								))
						.signWith(key)
						.compact();
	}
	
	public String generateRefreshToken(User user, String jti) { // jti -> unique jwt token id
		Instant now  = Instant.now();
		return Jwts.builder()
				.id(jti)
				.subject(user.getId().toString())
				.issuer(issuer)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
				.claim("typ", "refresh")
				.signWith(key)
				.compact();
				
	}
	
	public Jws<Claims> parse(String token){
		try {
			return Jwts.parser().verifyWith(key).build().parseSignedClaims(token); 
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean isAccessToken(String token) {
		Claims c = parse(token).getPayload();
		return "access".equals(c.get("typ"));
	}
	
	public boolean isRefreshToken(String token) {
		Claims c = parse(token).getPayload();
		return "refresh".equals(c.get("typ"));
	}
	
	public UUID getUserId(String token) {
		Claims c = parse(token).getPayload();
		return UUID.fromString(c.getSubject());
	}
	
	public String getJti(String token) {
		return parse(token).getPayload().getId();
	}
}
