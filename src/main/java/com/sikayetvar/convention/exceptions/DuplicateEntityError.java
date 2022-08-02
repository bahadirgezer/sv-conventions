package com.sikayetvar.convention.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class DuplicateEntityError extends RuntimeException {

    public DuplicateEntityError() {
        super("Entities must be unique.");
    }

    public DuplicateEntityError(String message) {
        super(message);
    }
}
