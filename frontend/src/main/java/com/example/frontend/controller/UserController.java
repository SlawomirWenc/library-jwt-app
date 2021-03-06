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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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

    public Cookie getCookieIfExist(HttpServletRequest request){
        if(request.getCookies() != null) {
            Optional<Cookie> authorization = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("jwt-token")).findFirst();
            if(authorization.isPresent()){
                return authorization.get();
            }
        }
        return new Cookie("", "");
    }

    @GetMapping("/adminPanel")
    public String getAdminPanelPage(Model model, HttpServletRequest request){
        Cookie cookie = getCookieIfExist(request);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cookie.getValue());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<User[]> exchange = restTemplate.exchange("http://localhost:4040/users", HttpMethod.GET, entity, User[].class);
        User[] body = exchange.getBody();
        model.addAttribute("users", body);
        return "adminPanel";
    }

    @GetMapping("/createUser")
    public String getCreateUserPage(){
        return "createUser";
    }

    @PostMapping("/createUser")
    public String createUser(User user, Model model, HttpServletRequest request){
        RestTemplate restTemplate = new RestTemplate();
        Cookie cookie = getCookieIfExist(request);

        String url = "http://localhost:4040/createUser";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cookie.getValue());

        Map<String, String> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("role", user.getRole());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody().equals("true")) {
            return "redirect:/adminPanel";
        }
        model.addAttribute("error", "true");
        return "createUser";
    }

    @PostMapping("/deleteUser")
    public String deleteById(String id, HttpServletRequest request){
        RestTemplate restTemplate = new RestTemplate();
        Cookie cookie = getCookieIfExist(request);
        String url = "http://localhost:4040/deleteUser?id="+id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cookie.getValue());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        restTemplate.postForEntity(url, entity, String.class);
        return "redirect:/adminPanel";
    }

    @GetMapping("/modifyUser")
    public String getModifyUserPage(String id, Model model, HttpServletRequest request){
        RestTemplate restTemplate = new RestTemplate();
        Cookie cookie = getCookieIfExist(request);
        String url = "http://localhost:4040/getUser?id="+id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cookie.getValue());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<User> userResponseEntity = restTemplate.postForEntity(url, entity, User.class);

        model.addAttribute("user", userResponseEntity.getBody());
        model.addAttribute("id", id);
        return "modifyUser";
    }

    @PostMapping("/updateUser")
    public String updateUser(User user, Model model, HttpServletRequest request){
        RestTemplate restTemplate = new RestTemplate();
        Cookie cookie = getCookieIfExist(request);
        String url = "http://localhost:4040/updateUser";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cookie.getValue());

        Map<String, String> map = new HashMap<>();
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("role", user.getRole());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody().equals("true")) {
            return "redirect:/adminPanel";
        }
        return "redirect:/modifyUser";
    }

}
