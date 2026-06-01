package in.vs.auth.service;

import in.vs.auth.dto.UserDto;

public interface UserService {
	UserDto createUser(UserDto userDto);
	UserDto findUserByEmail(String email);
	UserDto updateUser(UserDto userDto, String userId);
	void DeleteUser(String userId);
	UserDto getUserById(String userId);
	Iterable<UserDto> getAllUsers();
}
