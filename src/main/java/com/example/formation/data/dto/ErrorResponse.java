package com.example.formation.data.dto;

import java.time.Instant;

/**
 * ErrorResponse
 */
public record ErrorResponse(
    int status,
    String message,
    Instant timestamp) {
  public ErrorResponse(int status, String message) {
    this(status, message, Instant.now());
  }
}
