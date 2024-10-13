package com.codetest.controller;

import com.codetest.config.DisableSecurityConfig;
import com.codetest.model.Book;
import com.codetest.service.Library;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {DisableSecurityConfig.class})
@Import(LibraryController.class)
public class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Library library;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void assertBookIsCreated() throws Exception {
        // Mocking the service behavior
        final Book book = new Book("testIsbn", "New Title", "testAuthor", 2023, 10);
        when(library.createBook(book)).thenReturn(book);

        // Performing an HTTP POST request to create an employee
        ResultActions response = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(book)));

        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", CoreMatchers.is(book.getIsbn())));
        verify(library, times(1)).createBook(book);
    }

    @Test
    public void assertBookIsFetchedByIsbn() throws Exception {
        // Mocking the service behavior
        final Book book = new Book("testIsbn", "New Title", "testAuthor", 2023, 10);
        when(library.findBookByISBN("testIsbn")).thenReturn(book);

        // Performing an HTTP POST request to create an employee
        ResultActions response = mockMvc.perform(get("/books/isbn/testIsbn")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", CoreMatchers.is(book.getIsbn())));
        verify(library, times(1)).findBookByISBN("testIsbn");
    }

    @Test
    public void assertBookIsFetchedByAuthor() throws Exception {
        // Mocking the service behavior
        final Book book = new Book("testIsbn", "New Title", "testAuthor", 2023, 10);
        when(library.findBooksByAuthor("testAuthor")).thenReturn(Arrays.asList(book));

        // Performing an HTTP POST request to create an employee
        ResultActions response = mockMvc.perform(get("/books/author/testAuthor")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
        verify(library, times(1)).findBooksByAuthor("testAuthor");
    }

    @Test
    public void assertBookIsBorrowed() throws Exception {
        // Mocking the service behavior
        final Book book = new Book("testIsbn", "New Title", "testAuthor", 2023, 10);
        when(library.borrowBook("testIsbn")).thenReturn(book);

        // Performing an HTTP POST request to create an employee
        ResultActions response = mockMvc.perform(put("/books/borrow/isbn/testIsbn")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", CoreMatchers.is(book.getIsbn())));
        verify(library, times(1)).borrowBook("testIsbn");
    }

    @Test
    public void assertBookIsReturned() throws Exception {
        // Mocking the service behavior
        final Book book = new Book("testIsbn", "New Title", "testAuthor", 2023, 10);
        when(library.returnBook("testIsbn")).thenReturn(book);

        // Performing an HTTP POST request to create an employee
        ResultActions response = mockMvc.perform(put("/books/return/isbn/testIsbn")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isbn", CoreMatchers.is(book.getIsbn())));
        verify(library, times(1)).returnBook("testIsbn");
    }

    @Test
    public void assertBookIsRemoved() throws Exception {
        // Mocking the service behavior
        doNothing().when(library).removeBook("testIsbn");

        // Performing an HTTP POST request to create an employee
        ResultActions response = mockMvc.perform(delete("/books/isbn/testIsbn")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

}
