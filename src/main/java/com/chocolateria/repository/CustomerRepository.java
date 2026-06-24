package com.chocolateria.repository;

import com.chocolateria.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Page<Customer> findByActiveTrue(Pageable pageable);

    @Query("""
        SELECT c FROM Customer c
        WHERE c.active = true
          AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(c.email)     LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Customer> search(@Param("search") String search, Pageable pageable);
}
