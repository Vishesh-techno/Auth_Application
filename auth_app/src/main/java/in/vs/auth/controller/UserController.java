package in.vs.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.vs.auth.dto.UserDto;
import in.vs.auth.service.UserService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
		return ResponseEntity.ok(userService.createUser(userDto));
	}

	@GetMapping
	public ResponseEntity<Iterable<UserDto>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
		return ResponseEntity.ok(userService.findUserByEmail(email));
	}

	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable("userId") String userId) {
		userService.DeleteUser(userId);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable("userId") String userId) {
		return ResponseEntity.ok(userService.updateUser(userDto, userId));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> getUserById(@PathVariable("userId") String userId) {
		return ResponseEntity.ok(userService.getUserById(userId));
	}
}
