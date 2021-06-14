package com.example.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {

    @GetMapping("/")
    public String getIndexPage(){
        return "index";
    }

    @GetMapping("/signIn")
    public String getSignInPage(){
        return "signIn";
    }

    @GetMapping("/signUp")
    public String getSignUpPage(){
        return "signUp";
    }
}
