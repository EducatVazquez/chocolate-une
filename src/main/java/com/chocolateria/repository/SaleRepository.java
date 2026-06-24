package com.chocolateria.repository;

import com.chocolateria.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("""
        SELECT s FROM Sale s
        JOIN FETCH s.customer c
        WHERE (:customerId IS NULL OR c.id = :customerId)
          AND (:status IS NULL OR s.status = :status)
          AND (CAST(:from AS LocalDateTime) IS NULL OR s.saleDate >= :from)
          AND (CAST(:to AS LocalDateTime) IS NULL OR s.saleDate <= :to)
        """)
    Page<Sale> findByFilters(
            @Param("customerId") Long customerId,
            @Param("status") Sale.Status status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @Query("SELECT s FROM Sale s JOIN FETCH s.customer JOIN FETCH s.details d JOIN FETCH d.product WHERE s.id = :id")
    java.util.Optional<Sale> findByIdWithDetails(@Param("id") Long id);
}
