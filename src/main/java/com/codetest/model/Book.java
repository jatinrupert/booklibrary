package com.codetest.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Integer publicationYear;

    @Column(nullable = true)
    private volatile AtomicInteger avaliableCopies;

    @Column(nullable = true)
    private Integer totalAvaliableCopies = 0;

    @PrePersist
    public void prePersist() {
        if(this.totalAvaliableCopies == null) {
            this.totalAvaliableCopies = 0;
        }
        this.avaliableCopies = new AtomicInteger(totalAvaliableCopies);
    }

    public Book() {
        //hibernate default
    }

    public Book(String isbn, String title, String author, Integer publicationYear, Integer totalAvaliableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.totalAvaliableCopies = totalAvaliableCopies;
        this.avaliableCopies = new AtomicInteger(totalAvaliableCopies);
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public AtomicInteger getAvaliableCopies() {
        return avaliableCopies;
    }

    public Integer getTotalAvaliableCopies() {
        return totalAvaliableCopies;
    }

    public Book reduceCopy() {
        this.avaliableCopies.decrementAndGet();
        return this;
    }

    public Book increaseCopy() {
        this.avaliableCopies.incrementAndGet();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
