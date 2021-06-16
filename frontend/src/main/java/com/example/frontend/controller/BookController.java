package com.example.frontend.controller;

import com.example.frontend.model.Book;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BookController {

    @GetMapping("/")
    public String getIndexPage(Model model){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Book[]> exchange = restTemplate.exchange("http://localhost:4040/books", HttpMethod.GET, HttpEntity.EMPTY, Book[].class);
        Book[] books = exchange.getBody();
        model.addAttribute("books", books);
        return "index";
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

    @GetMapping("/library")
    public String getLibraryPage(Model model, HttpServletRequest request){
        Cookie cookie = getCookieIfExist(request);
        RestTemplate restTemplate = new RestTemplate();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        String url = "http://localhost:4040/booksByUsername?username="+currentPrincipalName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cookie.getValue());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Book[]> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, Book[].class);
        Book[] books = exchange.getBody();
        model.addAttribute("books", books);
        return "library";
    }

    @PostMapping("/addBook")
    public String addBook(Long id, HttpServletRequest request){
        Cookie cookie = getCookieIfExist(request);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cookie.getValue());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        restTemplate.postForEntity("http://localhost:4040/addBook?id="+id, entity, String.class);
        return "redirect:/library";
    }

    @PostMapping("/removeBook")
    public String removeBook(Long id, HttpServletRequest request){
        Cookie cookie = getCookieIfExist(request);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(cookie.getValue());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        restTemplate.postForEntity("http://localhost:4040/removeBook?id="+id, entity, String.class);
        return "redirect:/library";
    }



}
