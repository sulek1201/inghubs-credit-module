package com.inghubs.credit.runner;

import com.inghubs.credit.entity.Customer;
import com.inghubs.credit.entity.User;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class MyCommandLineRunner implements CommandLineRunner {


    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {


        User user = User.builder()
                .id(1L)
                .password(bCryptPasswordEncoder.encode("1234"))
                .username("anilsulekoglu")
                .userRole("ROLE_USER")
                .build();

        userRepository.save(user);

        Customer customer = Customer.builder()
                .id(1L)
                .userId(user)
                .name("Anil")
                .surname("Sulekoglu")
                .creditLimit(BigDecimal.valueOf(100000))
                .usedCreditLimit(BigDecimal.ZERO)
                .build();

        customerRepository.save(customer);
    }
}
