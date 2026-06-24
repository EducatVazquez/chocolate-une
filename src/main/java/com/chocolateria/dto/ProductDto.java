package com.chocolateria.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductDto(
        Long id,

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(min = 2, max = 200)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal price,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        String imageUrl,

        @NotNull(message = "La categoría es obligatoria")
        Long categoryId,

        String categoryName,

        Boolean active
) {
    public ProductDto {
        if (active == null) active = true;
    }
}
