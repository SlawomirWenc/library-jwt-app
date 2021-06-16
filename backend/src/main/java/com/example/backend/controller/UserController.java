package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
public class UserController {

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @Value("${secret}")
    private String secret;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public Iterable<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping("/signUp")
    public boolean signUp(@RequestBody User user){
        Optional<User> userByUsername = userService.getUserByUsername(user.getUsername());
        if(userByUsername.isEmpty()){
            userService.saveUser(new User(user.getUsername(), passwordEncoder.encode(user.getPassword()), "ROLE_USER"));
            return true;
        }
        return false;
    }

    @PostMapping("/signIn")
    public String signIn(@RequestBody User user){
        Optional<User> userByUsername = userService.getUserByUsername(user.getUsername());
        if(userByUsername.isPresent()){
            if(passwordEncoder.matches(user.getPassword(), userByUsername.get().getPassword())){
                return createToken(user.getUsername(), userByUsername.get().getRole());
            }
        }
        return "";
    }

    private String createToken(String username, String role) {
        long currentTimeMillis = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(username)
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + 600000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return token;
    }

    @PostMapping("/createUser")
    public boolean createUser(@RequestBody User user){
        Optional<User> userByUsername = userService.getUserByUsername(user.getUsername());
        if(userByUsername.isEmpty()){
            userService.saveUser(new User(user.getUsername(), passwordEncoder.encode(user.getPassword()), user.getRole()));
            return true;
        }
        return false;
    }

    @PostMapping("/deleteUser")
    public void deleteById(@RequestParam String id){
        userService.deleteById(Long.valueOf(id));
    }

    @PostMapping("/getUser")
    public Optional<User> getUserById(@RequestParam String id){
        return userService.getUserById(Long.valueOf(id));
    }

    @PostMapping("/updateUser")
    public boolean updateUser(@RequestBody User user){
        Optional<User> userById = userService.getUserById(user.getId());
        if(userById.isPresent()){
            User tempUser = userById.get();
            tempUser.setUsername(user.getUsername());
            tempUser.setPassword(passwordEncoder.encode(user.getPassword()));
            tempUser.setRole(user.getRole());
            userService.saveUser(tempUser);
            return true;
        }
        return false;
    }
}
