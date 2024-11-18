package com.inghubs.credit.service.impl;

import com.inghubs.credit.entity.Customer;
import com.inghubs.credit.exception.CustomerNotFoundException;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Long getCustomerIdFromUserId(Long userId) {
        Customer customer = customerRepository.findIdByUserId_Id(userId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for user ID: " + userId));
        return customer.getId();
    }
}
