package in.vs.auth.exceptions;

@SuppressWarnings("serial")
public class ResourceNotFoundException extends RuntimeException{
	public ResourceNotFoundException(String message) {
		super(message);
	}
	
	public ResourceNotFoundException() {
		super("Resource Not Found");
	}

}
