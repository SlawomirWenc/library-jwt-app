package com.example.backend.controller;

import com.example.backend.service.BookService;
import com.example.backend.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public Iterable<Book> getAllBooks(){
        return bookService.getAllBooks();
    }
}
