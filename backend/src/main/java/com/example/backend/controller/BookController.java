package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.service.BookService;
import com.example.backend.model.Book;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    private BookService bookService;

    private UserService userService;

    @Autowired
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/books")
    public Iterable<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/booksByUsername")
    public Iterable<Book> getAllBooksByUsername(@RequestParam String username){
        Optional<User> userByUsername = userService.getUserByUsername(username);
        if(userByUsername.isPresent()){
            return userByUsername.get().getBookList();
        }
        return new ArrayList<>();
    }

    @PostMapping("/addBook")
    public boolean addBook(@RequestParam Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Optional<User> userByUsername = userService.getUserByUsername(currentPrincipalName);
        if(userByUsername.isPresent()){
            User userTemp = userByUsername.get();
            List<Book> bookList = userTemp.getBookList();
            Optional<Book> bookById = bookService.getBookById(id);
            if(bookById.isPresent()){
                bookList.add(bookById.get());
                userTemp.setBookList(bookList);
                userService.saveUser(userTemp);
            }
        }
        return true;
    }

    @PostMapping("/removeBook")
    public boolean removeBook(@RequestParam Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Optional<User> userByUsername = userService.getUserByUsername(currentPrincipalName);
        if(userByUsername.isPresent()){
            User userTemp = userByUsername.get();
            List<Book> bookList = userTemp.getBookList();
            Optional<Book> first = bookList.stream().filter(book -> book.getId() == id).findFirst();
            if(first.isPresent()){
                bookList.remove(first.get());
                userTemp.setBookList(bookList);
                userService.saveUser(userTemp);
                return true;
            }
        }
       return false;
    }
}
