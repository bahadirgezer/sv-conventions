package compaintvar.convention.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MsDBOperationException extends RuntimeException {

    public MsDBOperationException() {
        super("Database operation failed.");
    }

    public MsDBOperationException(String message) {
        super(message);
    }
}
