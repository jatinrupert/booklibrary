package com.codetest.service;

import com.codetest.exception.BookNotFoundException;
import com.codetest.model.Book;
import com.codetest.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class LibraryUnitTest {

    @Autowired
    private Library library;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void assertBookIsCreated() {
        assertEquals(0, bookRepository.findByAuthor("testAuthor").size());
        assertTrue(bookRepository.findByIsbn("testIsbn").isEmpty());

        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10));

        assertEquals(1, bookRepository.findByAuthor("testAuthor").size());
        final Optional<Book> book = bookRepository.findByIsbn("testIsbn");
        assertFalse(book.isEmpty());
        assertEquals(10, book.get().getAvaliableCopies().intValue());
        assertEquals(10, book.get().getTotalAvaliableCopies());
    }

    @Test
    public void assertBookIsNotCreatedAndErrorsForUniqueValues() {
        assertTrue(bookRepository.findByIsbn("testIsbn").isEmpty());

        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10));

        assertThrows(
                DataIntegrityViolationException.class,
                () ->  library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10)),
                "Expected createBook() to throw, but it didn't"
        );
    }

    @Test
    public void assertFindByIsbnReturnsBook() {
        assertTrue(bookRepository.findByIsbn("testIsbn").isEmpty());

        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10));
        final Book book = library.findBookByISBN("testIsbn");

        assertEquals(2023, book.getPublicationYear());
        assertEquals("New Title", book.getTitle());
    }

    @Test
    public void assertFindByNonExistingIsbnReturnError() {
        BookNotFoundException thrown = assertThrows(
                BookNotFoundException.class,
                () ->  library.findBookByISBN("testIsbn"),
                "Expected findBookByISBN() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Book was not found using isbn : testIsbn"));
    }

    @Test
    public void assertBorrowBookReducesCopies() {
        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10));

        final Book book = library.borrowBook("testIsbn");
        assertEquals(9, book.getAvaliableCopies().intValue());
        assertEquals(10, book.getTotalAvaliableCopies());
    }

    @Test
    public void assertBorrowZeroCopiesReturnError() {
        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 1));
        library.borrowBook("testIsbn");

        BookNotFoundException thrown = assertThrows(
                BookNotFoundException.class,
                () ->   library.borrowBook("testIsbn"),
                "Expected borrowBook() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("No copies exist to borrow for isbn : testIsbn"));
    }

    @Test
    public void assertReturnBookReducesCopies() {
        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10));
        library.borrowBook("testIsbn");
        library.borrowBook("testIsbn");

        final Book book = library.returnBook("testIsbn");
        assertEquals(9, book.getAvaliableCopies().intValue());
        assertEquals(10, book.getTotalAvaliableCopies());
    }

    @Test
    public void assertReturnOverMaximumCopiesReturnError() {
        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10));

        BookNotFoundException thrown = assertThrows(
                BookNotFoundException.class,
                () ->   library.returnBook("testIsbn"),
                "Expected returnBook() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Return cannot be done as full limit of copies exist for isbn : testIsbn"));
    }

    @Test
    public void assertBooksIsremovedFromLibrary() {
        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10));
        assertEquals(1, bookRepository.findByAuthor("testAuthor").size());

        library.removeBook("testIsbn");

        assertEquals(0, bookRepository.findByAuthor("testAuthor").size());
    }

    @Test
    public void assertRemoveByNonExistingIsbnReturnError() {
        BookNotFoundException thrown = assertThrows(
                BookNotFoundException.class,
                () ->  library.removeBook("testIsbn"),
                "Expected removeBook() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Book was not found using isbn : testIsbn"));
    }

    @Test
    public void assertFindByAuthorReturnsRightAmountOfBooks() {
        library.createBook(new Book("testIsbn", "New Title", "testAuthor", 2023, 10));

        assertEquals(1, library.findBooksByAuthor("testAuthor").size());

        library.createBook(new Book("testIsbn1", "New New Title", "testNewAuthor", 2023, 10));

        assertEquals(1, library.findBooksByAuthor("testAuthor").size());

        library.createBook(new Book("testIsbn2", "New Another Title", "testAuthor", 2024, 10));

        assertEquals(2, library.findBooksByAuthor("testAuthor").size());
    }

    @Test
    public void assertSearchByNonExistentAuthorReturnError() {
        BookNotFoundException thrown = assertThrows(
                BookNotFoundException.class,
                () ->  library.findBooksByAuthor("testAuthor"),
                "Expected findBooksByAuthor() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Books were not found for author : testAuthor"));
    }

}
