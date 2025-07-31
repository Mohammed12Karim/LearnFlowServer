package com.example.formation.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TokenNotValid
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TokenNotValid extends RuntimeException {

}
