package com.chocolateria.service;

import com.chocolateria.dto.CustomerDto;
import com.chocolateria.entity.Customer;
import com.chocolateria.exception.BusinessException;
import com.chocolateria.exception.ResourceNotFoundException;
import com.chocolateria.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public Page<CustomerDto> findAll(String search, Pageable pageable) {
        Page<Customer> page = (search != null && !search.isBlank())
                ? customerRepository.search(search, pageable)
                : customerRepository.findByActiveTrue(pageable);
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public CustomerDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public CustomerDto create(CustomerDto dto) {
        customerRepository.findByEmailIgnoreCase(dto.email()).ifPresent(existing -> {
            throw new BusinessException("Ya existe un cliente con el email: " + dto.email());
        });
        Customer customer = new Customer();
        mapToEntity(dto, customer);
        return toDto(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDto update(Long id, CustomerDto dto) {
        Customer customer = getOrThrow(id);
        if (customerRepository.existsByEmailIgnoreCaseAndIdNot(dto.email(), id)) {
            throw new BusinessException("El email ya está registrado por otro cliente: " + dto.email());
        }
        mapToEntity(dto, customer);
        if (dto.active() != null) customer.setActive(dto.active());
        return toDto(customerRepository.save(customer));
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = getOrThrow(id);
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private void mapToEntity(CustomerDto dto, Customer customer) {
        customer.setFirstName(dto.firstName().trim());
        customer.setLastName(dto.lastName().trim());
        customer.setEmail(dto.email().trim().toLowerCase());
        customer.setPhone(dto.phone());
        customer.setAddress(dto.address());
    }

    private Customer getOrThrow(Long id) {
        return customerRepository.findById(id)
                .filter(Customer::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    public CustomerDto toDto(Customer c) {
        return new CustomerDto(
                c.getId(), c.getFirstName(), c.getLastName(),
                c.getEmail(), c.getPhone(), c.getAddress(), c.getActive()
        );
    }
}
