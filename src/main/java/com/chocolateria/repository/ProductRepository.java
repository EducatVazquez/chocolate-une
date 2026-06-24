package com.chocolateria.repository;

import com.chocolateria.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.category c
        WHERE p.active = true
          AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS String), '%')))
          AND (:categoryId IS NULL OR c.id = :categoryId)
        """)
    Page<Product> findByFilters(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.active = true ORDER BY p.name")
    java.util.List<Product> findAllActiveWithCategory();
}
