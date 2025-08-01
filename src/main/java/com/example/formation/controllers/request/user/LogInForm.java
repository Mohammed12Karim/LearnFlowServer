package com.example.formation.controllers.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** LogInForm */
public record LogInForm(
    @NotBlank @Email String email,
    @NotBlank(message = "password must be set") @Size(min = 8,message = "password must be longer than 8") String password) {
}
