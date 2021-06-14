package com.example.frontend;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @GetMapping("/signIn")
    public String getSignInPage(){
        return "signIn";
    }

    @GetMapping("/signUp")
    public String getSignUpPage(){
        return "signUp";
    }

    @PostMapping("/signUp")
    public String signUp(User user, String password_confirm, Model model){

        if(user.getPassword().equals(password_confirm)){

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:4040/signUp";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            Map<String, String> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("password", user.getPassword());
            map.put("role", "ROLE_USER");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (exchange.getStatusCode() == HttpStatus.OK) {
                if(exchange.getBody().equals("true")){
                    model.addAttribute("confirm", "true");
                    return "signIn";
                }
                model.addAttribute("userExist", "true");
                return "signUp";
            }
            model.addAttribute("error", "true");
            return "signUp";
        }
        model.addAttribute("passIn", "true");
        return "signUp";
    }

}
