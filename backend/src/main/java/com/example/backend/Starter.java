package com.example.backend;

import com.example.backend.model.Book;
import com.example.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Starter {

    private BookService bookService;

    @Autowired
    public Starter(BookService bookService) {
        this.bookService = bookService;
        Book book1 = new Book("Ulysses Moore. Tom 17. Godzina bitwy", "Pierdomenico Baccalario", "Olesiejuk", 2021, 260);
        Book book2 = new Book("Łowcy Diuny. Zakończenie cyklu Diuna. Tom 1", "Brian Herbert, Kevin J. Anderson", "Rebis", 2021, 592);
        Book book3 = new Book("Pieśń o Achillesie", "Madeline Miller", "Albatros", 2021, 384);
        bookService.addBook(book1);
        bookService.addBook(book2);
        bookService.addBook(book3);
    }
}
