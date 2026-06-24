package com.chocolateria.service;

import com.chocolateria.dto.SaleRequestDto;
import com.chocolateria.dto.SaleResponseDto;
import com.chocolateria.dto.SaleResponseDto.SaleDetailResponseDto;
import com.chocolateria.entity.Customer;
import com.chocolateria.entity.Product;
import com.chocolateria.entity.Sale;
import com.chocolateria.entity.SaleDetail;
import com.chocolateria.exception.BusinessException;
import com.chocolateria.exception.InsufficientStockException;
import com.chocolateria.exception.ResourceNotFoundException;
import com.chocolateria.repository.CustomerRepository;
import com.chocolateria.repository.ProductRepository;
import com.chocolateria.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<SaleResponseDto> findAll(Long customerId, String status, Pageable pageable) {
        Sale.Status saleStatus = null;
        if (status != null && !status.isBlank()) {
            try { saleStatus = Sale.Status.valueOf(status.toUpperCase()); }
            catch (IllegalArgumentException ex) {
                throw new BusinessException("Estado de venta inválido: " + status);
            }
        }
        return saleRepository.findByFilters(customerId, saleStatus, null, null, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public SaleResponseDto findById(Long id) {
        Sale sale = saleRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));
        return toDto(sale);
    }

    /**
     * Registra una venta: valida stock, descuenta unidades y persiste todo en una única transacción.
     */
    @Transactional
    public SaleResponseDto checkout(SaleRequestDto request) {
        Customer customer = customerRepository.findById(request.customerId())
                .filter(Customer::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.customerId()));

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(Sale.Status.CONFIRMED);
        sale.setNotes(request.notes());

        BigDecimal total = BigDecimal.ZERO;

        for (var item : request.details()) {
            Product product = productRepository.findById(item.productId())
                    .filter(Product::getActive)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", item.productId()));

            if (product.getStock() < item.quantity()) {
                throw new InsufficientStockException(
                        product.getName(), item.quantity(), product.getStock());
            }

            // Descontar stock
            product.setStock(product.getStock() - item.quantity());
            productRepository.save(product);

            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(item.quantity());
            detail.setUnitPrice(product.getPrice());
            detail.calculateSubtotal();

            sale.getDetails().add(detail);
            total = total.add(detail.getSubtotal());
        }

        sale.setTotal(total);
        Sale saved = saleRepository.save(sale);
        return toDto(saved);
    }

    @Transactional
    public SaleResponseDto cancel(Long id) {
        Sale sale = saleRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));

        if (sale.getStatus() == Sale.Status.CANCELLED) {
            throw new BusinessException("La venta ya se encuentra cancelada.");
        }

        // Revertir el stock
        for (SaleDetail detail : sale.getDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }

        sale.setStatus(Sale.Status.CANCELLED);
        return toDto(saleRepository.save(sale));
    }

    private SaleResponseDto toDto(Sale s) {
        var details = s.getDetails().stream()
                .map(d -> new SaleDetailResponseDto(
                        d.getId(),
                        d.getProduct().getId(),
                        d.getProduct().getName(),
                        d.getQuantity(),
                        d.getUnitPrice(),
                        d.getSubtotal()
                )).toList();

        return new SaleResponseDto(
                s.getId(),
                s.getCustomer().getId(),
                s.getCustomer().getFullName(),
                s.getSaleDate(),
                s.getTotal(),
                s.getStatus(),
                s.getNotes(),
                details
        );
    }
}
