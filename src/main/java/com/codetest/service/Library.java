package com.codetest.service;

import com.codetest.exception.BookNotFoundException;
import com.codetest.model.Book;
import com.codetest.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Library {

    @Autowired
    private BookRepository bookRepository;

    public Book createBook(Book book) {
        return this.bookRepository.save(book);
    }

    @Cacheable(value="books")
    public Book findBookByISBN(String isbn) {
        Optional<Book> book = this.bookRepository.findByIsbn(isbn);

        return book.orElseThrow(() -> new BookNotFoundException("Book was not found using isbn : " + isbn));
    }

    @CachePut(value="books")
    public Book borrowBook(String isbn) {
        final Book book = findBookByISBN(isbn);

        if(book.getAvaliableCopies().get() == 0) {
            throw new BookNotFoundException("No copies exist to borrow for isbn : " + isbn);
        }

        this.bookRepository.save(book.reduceCopy());
        return book;
    }

    @CachePut(value="books")
    public Book returnBook(String isbn) {
        final Book book = findBookByISBN(isbn);

        if(book.getAvaliableCopies().get() == book.getTotalAvaliableCopies()) {
            throw new BookNotFoundException("Return cannot be done as full limit of copies exist for isbn : " + isbn);
        }

        this.bookRepository.save(book.increaseCopy());
        return book;
    }

    @CacheEvict(value="books")
    public void removeBook(String isbn) {
        this.bookRepository.delete(findBookByISBN(isbn));
    }

    public List<Book> findBooksByAuthor(String author) {
        List<Book> books = this.bookRepository.findByAuthor(author);

        if(books.isEmpty()) {
            throw new BookNotFoundException("Books were not found for author : " + author);
        }

        return books;
    }
}
