package com.manorath.csye6225.service;

import com.manorath.csye6225.exception.*;
import com.manorath.csye6225.model.User;
import com.manorath.csye6225.repository.UserRepository;
import com.manorath.csye6225.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService extends GeneralExceptionHandler {

    // Autowired DAO
    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService() {}

    // Buisness Logic
    public User createUser(User user) {
        // Set account created and account updated
        user.setId(UUID.randomUUID().toString());
        user.setAccountCreated(new Date());
        user.setAccountUpdated(new Date());
        if(Utils.checkPassword(user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        } else {
            return null;
        }
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new EmailAlreadyInUseException("Email already in use");
        }
    }

    public User updateUser(User user) {
        // Set Account updated
        user.setAccountUpdated(new Date());

        if(Utils.checkPassword(user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        } else {
            return null;
        }
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new EmailAlreadyInUseException("Email already in use");
        }
    }

    public User getUser() {
        return null;
    }

    public User findUserByEmail(String email,String password) {
        User u = userRepository.findUserByEmail(email);
        if(u == null) {
            throw new PasswordNotValidException("Password not valid");
        }
        if(bCryptPasswordEncoder.matches(password,u.getPassword())) {
            return u;
        }
        else {
            throw new PasswordDoesNotMatchException("Email and password does not match");
        }
    }


}
