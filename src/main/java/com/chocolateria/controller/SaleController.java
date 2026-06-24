package com.chocolateria.controller;

import com.chocolateria.dto.SaleRequestDto;
import com.chocolateria.dto.SaleResponseDto;
import com.chocolateria.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public Page<SaleResponseDto> getAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "saleDate") Pageable pageable) {
        return saleService.findAll(customerId, status, pageable);
    }

    @GetMapping("/{id}")
    public SaleResponseDto getById(@PathVariable Long id) {
        return saleService.findById(id);
    }

    @PostMapping("/checkout")
    public ResponseEntity<SaleResponseDto> checkout(@Valid @RequestBody SaleRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.checkout(request));
    }

    @PatchMapping("/{id}/cancel")
    public SaleResponseDto cancel(@PathVariable Long id) {
        return saleService.cancel(id);
    }
}
