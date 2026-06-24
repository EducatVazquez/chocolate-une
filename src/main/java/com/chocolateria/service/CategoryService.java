package com.chocolateria.service;

import com.chocolateria.dto.CategoryDto;
import com.chocolateria.entity.Category;
import com.chocolateria.exception.BusinessException;
import com.chocolateria.exception.ResourceNotFoundException;
import com.chocolateria.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDto> findAll(String name, Pageable pageable) {
        Page<Category> page = (name != null && !name.isBlank())
                ? categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable)
                : categoryRepository.findByActiveTrue(pageable);
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        if (categoryRepository.findByNameIgnoreCase(dto.name()).isPresent()) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + dto.name());
        }
        Category category = new Category();
        category.setName(dto.name().trim());
        category.setDescription(dto.description());
        category.setActive(dto.active());
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = getOrThrow(id);
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(dto.name(), id)) {
            throw new BusinessException("Ya existe otra categoría con el nombre: " + dto.name());
        }
        category.setName(dto.name().trim());
        category.setDescription(dto.description());
        if (dto.active() != null) category.setActive(dto.active());
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = getOrThrow(id);
        boolean hasProducts = category.getProducts().stream().anyMatch(p -> p.getActive());
        if (hasProducts) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene productos activos asociados.");
        }
        category.setActive(false);
        categoryRepository.save(category);
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }

    public CategoryDto toDto(Category c) {
        return new CategoryDto(c.getId(), c.getName(), c.getDescription(), c.getActive());
    }
}
