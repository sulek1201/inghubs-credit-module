package com.inghubs.credit.repository;

import com.inghubs.credit.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findIdByUserId_Id(Long userId);
}

