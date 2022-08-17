package compaintvar.convention.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class MsDBOperationException extends RuntimeException {

    public MsDBOperationException() {
        super("Database operation failed.");
    }

    public MsDBOperationException(String message) {
        super(message);
    }
}