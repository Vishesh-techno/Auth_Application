package in.vs.auth.exceptions;

import java.util.Map;

import javax.security.auth.login.CredentialExpiredException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import in.vs.auth.dto.ApiError;
import in.vs.auth.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ 
		UsernameNotFoundException.class,
		BadCredentialsException.class,
		CredentialExpiredException.class,
		DisabledException.class 
		})
	public ResponseEntity<ApiError> handleAuthException(Exception e, HttpServletRequest request) {
		var apiError = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage(), request.getRequestURI());
		return ResponseEntity.badRequest().body(apiError);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
		ErrorResponse error = new ErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, "Internal Error");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
		ErrorResponse error = new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, "Internal Error");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(OtpException.class)
    public ResponseEntity<Map<String, String>> handleOtpException(OtpException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

}
