package com.example.backend.service;

import com.example.backend.model.Book;
import com.example.backend.repo.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    private BookRepo bookRepo;

    @Autowired
    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public Iterable<Book> getAllBooks(){
        return bookRepo.findAll();
    }

    public void addBook(Book book){
        bookRepo.save(book);
    }

    public void deleteBook(Long id){
        bookRepo.deleteById(id);
    }

    public Optional<Book> getBookById(Long id){
        return bookRepo.findById(id);
    }
}
