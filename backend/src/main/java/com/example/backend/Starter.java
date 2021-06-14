package com.example.backend;

import com.example.backend.model.Book;
import com.example.backend.model.User;
import com.example.backend.service.BookService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Starter {

    private BookService bookService;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public Starter(BookService bookService, UserService userService, PasswordEncoder passwordEncoder) {
        this.bookService = bookService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        Book book1 = new Book("Ulysses Moore. Tom 17. Godzina bitwy", "Pierdomenico Baccalario", "Olesiejuk", 2021, 260);
        Book book2 = new Book("Łowcy Diuny. Zakończenie cyklu Diuna. Tom 1", "Brian Herbert, Kevin J. Anderson", "Rebis", 2021, 592);
        Book book3 = new Book("Pieśń o Achillesie", "Madeline Miller", "Albatros", 2021, 384);
        bookService.addBook(book1);
        bookService.addBook(book2);
        bookService.addBook(book3);

        List<Book> bookList = new ArrayList<>();

        User user = new User("user", passwordEncoder.encode("user"), "ROLE_USER", bookList);
        userService.saveUser(user);
    }
}
