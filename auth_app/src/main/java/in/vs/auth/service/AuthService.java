package in.vs.auth.service;

import in.vs.auth.dto.UserDto;

public interface AuthService {
	UserDto registerUser(UserDto userDto);
}
