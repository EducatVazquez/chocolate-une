package com.chocolateria.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaleRequestDto(

        @NotNull(message = "El cliente es obligatorio")
        Long customerId,

        @NotEmpty(message = "La venta debe tener al menos un producto")
        @Valid
        List<SaleDetailRequestDto> details,

        String notes
) {}
