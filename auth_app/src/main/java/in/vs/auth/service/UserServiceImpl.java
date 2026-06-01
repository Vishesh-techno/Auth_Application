package in.vs.auth.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import in.vs.auth.dto.UserDto;
import in.vs.auth.entities.Provider;
import in.vs.auth.entities.User;
import in.vs.auth.exceptions.ResourceNotFoundException;
import in.vs.auth.helper.UserHelper;
import in.vs.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	@Override
	public UserDto createUser(UserDto userDto) {
		if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}
		if (userRepository.existsByEmail(userDto.getEmail())) {
			throw new IllegalArgumentException("User With Given Email already exists");
		}
		User user = modelMapper.map(userDto, User.class);
		user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL);
		User savedUser = userRepository.save(user);
		return modelMapper.map(savedUser, UserDto.class);
	}

	@Override
	public UserDto findUserByEmail(String email) {

		User user = userRepository.findByEmail(email)
		        .orElseThrow(() -> new ResourceNotFoundException("User Not Found with given email id!"));
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto updateUser(UserDto userDto, String userId) {
		UUID uid = UserHelper.parseUUID(userId);
		User user = userRepository
				.findById(uid)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found with give id"));
		if(userDto.getName() != null) user.setName(userDto.getName());
		if(userDto.getImage() != null) user.setImage(userDto.getImage());
		if(userDto.getProvider() != null) user.setProvider(userDto.getProvider());
		if(userDto.getPassword() != null) user.setPassword(userDto.getPassword());
//		user.setEnabel(userDto.isEnabel());
		User updatedUser = userRepository.save(user);
		return modelMapper.map(updatedUser, UserDto.class);
	}

	@Override
	public void DeleteUser(String userId) {
		UUID uid = UserHelper.parseUUID(userId);
		User user = userRepository.findById(uid).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		userRepository.delete(user);
	}

	@Override
	public UserDto getUserById(String userId) {
		UUID uid = UserHelper.parseUUID(userId);
		User user = userRepository.findById(uid).orElseThrow(() -> new ResourceNotFoundException("User Not Found With this id"));
		return modelMapper.map(user, UserDto.class);
	}
 
	@Override
	public Iterable<UserDto> getAllUsers() {
		return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
	}

}
