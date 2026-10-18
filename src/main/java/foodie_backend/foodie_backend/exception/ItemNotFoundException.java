package foodie_backend.foodie_backend.exception;

import org.springframework.http.HttpStatus;

public class ItemNotFoundException extends AppException {

    public ItemNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
