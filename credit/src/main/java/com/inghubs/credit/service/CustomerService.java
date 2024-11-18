package com.inghubs.credit.service;


import org.springframework.stereotype.Service;


@Service
public interface CustomerService {

    Long getCustomerIdFromUserId(Long userId);

}
