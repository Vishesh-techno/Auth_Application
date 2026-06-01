package in.vs.auth.dto;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import in.vs.auth.entities.Provider;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	
	private UUID id;
	private String name;
	private String email;
	private String password;
	private String image;
//	private boolean enabel = true;
	
	@Column(updatable = false)
	private Instant createdAt = Instant.now();
	private Instant updatedAt = Instant.now();
	private Provider provider;
	private Set<RoleDto> roles = new HashSet<>();
	
}
