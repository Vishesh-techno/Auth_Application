package in.vs.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.vs.auth.dto.UserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	@Override
	public UserDto registerUser(UserDto userDto) {
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
		UserDto userDto1 = userService.createUser(userDto);
		return userDto1;
	}
 
}
