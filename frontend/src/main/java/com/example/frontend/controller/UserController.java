package com.example.frontend.controller;

import com.example.frontend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class UserController {

    @Value("${secret}")
    private String secret;

    @GetMapping("/signIn")
    public String getSignInPage(){
        return "signIn";
    }

    @PostMapping("/signIn")
    public String signIn(User user, HttpServletResponse res){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:4040/signIn";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if(!response.getBody().equals("")){
            res.addCookie(new Cookie("jwt-token", response.getBody()));
        }

        Claims body = Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(response.getBody()).getBody();
        String role = body.get("role").toString();

        Set<SimpleGrantedAuthority> userTemp = Collections.singleton(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(user.getUsername(), null, userTemp);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        return "redirect:/";
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
