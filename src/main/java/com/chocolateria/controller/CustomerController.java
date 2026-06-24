package com.chocolateria.controller;

import com.chocolateria.dto.CustomerDto;
import com.chocolateria.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Page<CustomerDto> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        return customerService.findAll(search, pageable);
    }

    @GetMapping("/{id}")
    public CustomerDto getById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> create(@Valid @RequestBody CustomerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(dto));
    }

    @PutMapping("/{id}")
    public CustomerDto update(@PathVariable Long id, @Valid @RequestBody CustomerDto dto) {
        return customerService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
