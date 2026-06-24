package com.chocolateria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerDto(
        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email,

        @Size(max = 20)
        String phone,

        @Size(max = 255)
        String address,

        Boolean active
) {
    public CustomerDto {
        if (active == null) active = true;
    }
}
