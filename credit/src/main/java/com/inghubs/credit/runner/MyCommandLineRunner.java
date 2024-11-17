package com.inghubs.credit.runner;

import com.inghubs.credit.entity.User;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {


        User user = User.builder()
                .id(1L)
                .password(bCryptPasswordEncoder.encode("1234"))
                .username("test-user")
                .userRole("ROLE_USER")
                .build();

        userRepository.save(user);
    }
}
