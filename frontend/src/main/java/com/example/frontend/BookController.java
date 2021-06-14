package com.example.frontend;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

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

}
