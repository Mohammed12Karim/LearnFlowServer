package com.example.formation.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** NotAuthorizedException */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException() {
    }

    public NotAuthorizedException(String msg) {
        super(msg);
    }
}
