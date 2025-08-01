package com.example.formation.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.formation.controllers.exception.BadRequestException;
import com.example.formation.controllers.exception.NotAuthorizedException;
import com.example.formation.controllers.exception.NotFoundException;
import com.example.formation.data.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFoundException(NotFoundException ex) {
    return new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage() != null ? ex.getMessage() : "Resource not found");
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBadRequestException(BadRequestException e) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), (e.getMessage() != null) ? e.getMessage() : "Bad Request");
  }


  @ExceptionHandler(NotAuthorizedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorResponse handleNotAuthorizedException(NotAuthorizedException e) {
    return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
        (e.getMessage() != null) ? e.getMessage() : "UNAUTHORIZED");
  }
}
