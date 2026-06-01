package in.vs.auth.entities;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails{
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name="user_id")
	private UUID id;
	@Column(name="user_name", length=500)
	private String name;
	@Column(name="user_email", unique=true, length=300)
	private String email;
	private String password;
	private String image;
	private boolean enabel = true;
	private Instant createdAt = Instant.now();
	private Instant updatedAt = Instant.now();
//	private String gender;
//	private Address address;
	private String providerId;
	
	@Enumerated(EnumType.STRING)
	private Provider provider;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	@PrePersist
	protected void onCreate() {
		Instant now = Instant.now();
		if(createdAt == null) {
			createdAt = now;
		}
		updatedAt = now;
	}
	
	@PreUpdate
	protected void onUpdate() {
		Instant now = Instant.now();
		updatedAt = now;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = roles
				                           .stream()
				                           .map(role -> new SimpleGrantedAuthority(role.getName()))
				                           .toList(); 
		
		return authorities;
	}

	@Override
	public String getUsername() {
		return this.email;
	}
	
	@Override
	public String getPassword() {
	    return this.password;
	}
	
}
