package com.chocolateria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDto(
        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100)
        String name,

        @Size(max = 300)
        String description,

        Boolean active
) {
    public CategoryDto {
        if (active == null) active = true;
    }
}
