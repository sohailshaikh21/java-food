package foodie_backend.foodie_backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderException extends AppException {

    public InvalidOrderException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
