package com.inghubs.credit.service;


import com.inghubs.credit.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User findByUserName(String userName);

    Long getUserIdByToken(String jwtToken);
}
