package com.chocolateria.dto;

import com.chocolateria.entity.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponseDto(
        Long id,
        Long customerId,
        String customerName,
        LocalDateTime saleDate,
        BigDecimal total,
        Sale.Status status,
        String notes,
        List<SaleDetailResponseDto> details
) {
    public record SaleDetailResponseDto(
            Long id,
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {}
}
