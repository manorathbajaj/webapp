package com.manorath.csye6225.controller;


import com.manorath.csye6225.exception.*;
import com.manorath.csye6225.model.User;

import com.manorath.csye6225.service.UserService;
import com.manorath.csye6225.util.Utils;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;



@RestController
public class UserController extends GeneralExceptionHandler {

    @Autowired
    UserService userService;

    @Autowired
    private StatsDClient statsd;

    @RequestMapping(value = "v1/user",
    method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public String createUser(@Valid @RequestBody User user,
                           HttpServletResponse response) {
        statsd.incrementCounter("UserHttpPost");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if(user.getAccountCreated()!= null || user.getAccountUpdated()!= null)
        {
            throw new FiledNotAllowedException("Fields not allowed");
        }
        User u = this.userService.createUser(user);
        if(u == null) {
            throw new PasswordNotValidException("Please enter a valid password");
        }
        response.setHeader("description","User created");
        stopWatch.stop();
        statsd.recordExecutionTime("UserHttpPost",stopWatch.getLastTaskTimeMillis());
        return Utils.toJsonString(u);
    }

    @RequestMapping(value = "v1/user/self",
            method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public String showUser(@RequestHeader(value = "Authorization")String auth) {
        String cred[] = Utils.decode(auth);
        statsd.incrementCounter("UserHttpGet");
        //StopWatch stopWatch = new StopWatch();
        User u;
        try {
            u = userService.findUserByEmail(cred[0], cred[1]);
        } catch (NullPointerException e) {
            throw new PasswordDoesNotMatchException("Password does not match");
        }
        //stopWatch.stop();
        //statsd.recordExecutionTime("UserHttpGet",stopWatch.getLastTaskTimeMillis());
        return Utils.toJsonString(u);
    }

    @RequestMapping(value = "v1/user/self",
            method = RequestMethod.PUT,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@RequestHeader(value = "Authorization")String auth,@Valid @RequestBody User user) {

        statsd.incrementCounter("UserHttpPut");
        //StopWatch stopWatch = new StopWatch();
        String cred[] = Utils.decode(auth);
        User u = userService.findUserByEmail(cred[0],cred[1]);
        if(user.getAccountCreated()!= null || user.getAccountUpdated()!= null)
        {
            throw new FiledNotAllowedException("Fields not allowed");
        }
        if(!user.getEmail().equals(u.getEmail())) {
            throw new UserDontMatchException("Authenticated user and provided email dont match");
        }
        if(!user.getEmail().equals(u.getEmail())) {
          throw new FiledNotAllowedException("not allowed");
        }
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        if(Utils.checkPassword(user.getPassword()))
        {
            u.setPassword(user.getPassword());
          //  stopWatch.stop();
           // statsd.recordExecutionTime("UserHttpPut",stopWatch.getLastTaskTimeMillis());
        } else {
            throw new PasswordNotValidException("Please Enter a valid password");
        }
        if(userService.updateUser(u) == null) {
            throw new PasswordNotValidException("Please enter a valid password");
        }
    }
}
