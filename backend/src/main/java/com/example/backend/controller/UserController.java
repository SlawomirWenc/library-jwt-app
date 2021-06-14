package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Iterable<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping("/signUp")
    public boolean signUp(@RequestBody User user){
        Optional<User> userByUsername = userService.getUserByUsername(user.getUsername());
        if(userByUsername.isEmpty()){
            userService.saveUser(user);
            return true;
        }
        return false;
    }

}
