package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Iterable<User> getAllUsers(){
        return userRepo.findAll();
    }

    public Optional<User> getUserByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public void saveUser(User user){
        userRepo.save(user);
    }
}
