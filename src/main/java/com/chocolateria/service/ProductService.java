package com.chocolateria.service;

import com.chocolateria.dto.ProductDto;
import com.chocolateria.entity.Category;
import com.chocolateria.entity.Product;
import com.chocolateria.exception.BusinessException;
import com.chocolateria.exception.ResourceNotFoundException;
import com.chocolateria.repository.CategoryRepository;
import com.chocolateria.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> findAll(String name, Long categoryId, Pageable pageable) {
        return productRepository.findByFilters(
                (name != null && !name.isBlank()) ? name : null,
                categoryId,
                pageable
        ).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> findAllActive() {
        return productRepository.findAllActiveWithCategory().stream().map(this::toDto).toList();
    }

    @Transactional
    public ProductDto create(ProductDto dto) {
        Category category = getCategoryOrThrow(dto.categoryId());
        Product product = new Product();
        mapToEntity(dto, product, category);
        return toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        Product product = getOrThrow(id);
        Category category = getCategoryOrThrow(dto.categoryId());
        mapToEntity(dto, product, category);
        if (dto.active() != null) product.setActive(dto.active());
        return toDto(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = getOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private void mapToEntity(ProductDto dto, Product product, Category category) {
        product.setName(dto.name().trim());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        if (dto.stock() < 0) throw new BusinessException("El stock no puede ser negativo");
        product.setStock(dto.stock());
        product.setImageUrl(dto.imageUrl());
        product.setCategory(category);
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(Category::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", categoryId));
    }

    public ProductDto toDto(Product p) {
        return new ProductDto(
                p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getStock(), p.getImageUrl(),
                p.getCategory().getId(), p.getCategory().getName(),
                p.getActive()
        );
    }
}
