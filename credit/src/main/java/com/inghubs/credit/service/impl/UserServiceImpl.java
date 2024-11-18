package com.inghubs.credit.service.impl;

import com.inghubs.credit.entity.User;
import com.inghubs.credit.repository.UserRepository;
import com.inghubs.credit.security.JwtTokenUtil;
import com.inghubs.credit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("UserServiceImpl")
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUserName(String userName) {
        User user = userRepository.findByUsername(userName);
        if(user == null){
            throw new RuntimeException("User not found");
        }
        return user;
    }

    @Override
    public Long getUserIdByToken(String jwtToken) {
        String userName = JwtTokenUtil.parseUserNameFromJwt(jwtToken);
        return findByUserName(userName).getId();
    }
}
