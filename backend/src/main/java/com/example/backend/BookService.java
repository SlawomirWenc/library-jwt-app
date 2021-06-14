package com.example.backend;

import org.springframework.stereotype.Service;

@Service
public class BookService {

    private BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public Iterable<Book> getAllBooks(){
        return bookRepo.findAll();
    }

    public void addBook(Book book){
        bookRepo.save(book);
    }
}
