package com.codetest.controller;

import com.codetest.model.Book;
import com.codetest.service.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class LibraryController {

    @Autowired
    private Library library;

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        return new ResponseEntity<>(this.library.createBook(book), HttpStatus.CREATED);
    }

    @GetMapping("/isbn/{isbn}")
    public Book findBookByISBN(@PathVariable("isbn") String isbn) {
        return this.library.findBookByISBN(isbn);
    }

    @GetMapping("/author/{author}")
    public List<Book> findBooksByAuthor(@PathVariable("author") String author) {
        return this.library.findBooksByAuthor(author);
    }

    @PutMapping("/borrow/isbn/{isbn}")
    public Book borrowBook(@PathVariable("isbn") String isbn) {
        return this.library.borrowBook(isbn);
    }

    @PutMapping("/return/isbn/{isbn}")
    public Book returnBook(@PathVariable("isbn") String isbn) {
        return this.library.returnBook(isbn);
    }

    @DeleteMapping("/isbn/{isbn}")
    public void removeBook(@PathVariable("isbn") String isbn) {
       this.library.removeBook(isbn);
    }

}
