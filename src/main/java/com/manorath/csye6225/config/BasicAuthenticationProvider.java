package com.manorath.csye6225.config;

import com.manorath.csye6225.exception.GeneralExceptionHandler;
import com.manorath.csye6225.exception.PasswordNotValidException;
import com.manorath.csye6225.model.User;
import com.manorath.csye6225.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class BasicAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        if(userService.findUserByEmail(email,password)!=null) {
            return new UsernamePasswordAuthenticationToken(email, password,new ArrayList<>());
        }
        else {
            throw new PasswordNotValidException("The password is not valid");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
