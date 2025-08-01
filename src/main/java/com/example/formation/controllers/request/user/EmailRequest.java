package com.example.formation.controllers.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {
    @NotBlank(message = "email is nessecary")
    @Email()
    private String email;
}
