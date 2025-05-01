package com.github.silviacristinaa.library.resources.bookIntegration;

import com.github.silviacristinaa.library.entities.Book;
import com.github.silviacristinaa.library.enums.BookStatusEnum;
import com.github.silviacristinaa.library.repositories.BookRepository;
import com.github.silviacristinaa.library.resources.integrations.IntegrationTests;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookResourceIntegrationTest extends IntegrationTests {

    private static final String NOT_FOUND_MSG = "Not found";
    private static final String BOOK_NOT_FOUND = "Book %s not found";

    private static final String TITLE = "Test";
    private static final String AUTHOR = "test";

    private String bookId;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @Order(1)
    public void whenTryCreateWithInvalidFieldsReturnBadRequest() throws Exception {
        mvc.perform(post("/books").headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                BookResourceIntegrationBody.bookException())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Arguments not valid")));
    }

    @Test
    @Order(2)
    public void whenCreateBookReturnCreated() throws Exception {
        mvc.perform(post("/books").headers(mockHttpHeaders())
                .content(objectMapper.writeValueAsString(
                        BookResourceIntegrationBody.bookCreate())))
                .andExpect(status().isCreated())
                .andDo(i -> bookId = getIdByLocation(i.getResponse().getHeader("Location")));
    }

    @Test
    @Order(3)
    public void whenFindAllReturnSuccess() throws Exception {
        mvc.perform(get("/books").headers(mockHttpHeaders()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].title", is (TITLE)))
                .andExpect(jsonPath("content[0].author", is(AUTHOR)))
                .andExpect(jsonPath("content[0].status", is("AVAILABLE")));
    }

    @Test
    @Order(4)
    public void whenTryFindByIdWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(get("/books/{id}", 999).headers(mockHttpHeaders()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(NOT_FOUND_MSG)))
                .andExpect(jsonPath("errors.[0]", is(String.format(BOOK_NOT_FOUND, 999))));
    }

    @Test
    @Order(5)
    public void whenFindByIdWithCorrectIdReturnSuccess() throws Exception {
        mvc.perform(get("/books/{id}", bookId).headers(mockHttpHeaders()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is(TITLE)))
                .andExpect(jsonPath("author", is(AUTHOR)))
                .andExpect(jsonPath("status", is("AVAILABLE")));
    }

    @Test
    @Order(6)
    public void whenTryUpdateBookStatusWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(patch("/books/{id}", 999).headers(mockHttpHeaders())
                .content(objectMapper.writeValueAsString(
                        BookResourceIntegrationBody.updateBookStatus())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(NOT_FOUND_MSG)))
                .andExpect(jsonPath("errors.[0]", is(String.format(BOOK_NOT_FOUND, 999))));
    }

    @Test
    @Order(7)
    public void whenUpdateBookStatusWithCorrectIdReturnNoContent() throws Exception {
        mvc.perform(patch("/books/{id}", bookId).headers(mockHttpHeaders())
                .content(objectMapper.writeValueAsString(
                        BookResourceIntegrationBody.updateBookStatus())))
                .andExpect(status().isNoContent());

        Optional<Book> book = bookRepository.findById(Long.valueOf(bookId));

        assertTrue(book.isPresent());
        assertEquals(book.get().getTitle(), TITLE);
        assertEquals(book.get().getAuthor(), AUTHOR);
        assertEquals(book.get().getStatus(), BookStatusEnum.BORROWED);
    }

    @Test
    @Order(8)
    public void whenTryUpdateBookWithInvalidFieldsReturnBadRequest() throws Exception {
        mvc.perform(put("/books/{id}", bookId).headers(mockHttpHeaders())
                .content(objectMapper.writeValueAsString(
                        BookResourceIntegrationBody.bookException())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Arguments not valid"));
    }

    @Test
    @Order(9)
    public void whenTryUpdateBookWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(put("/books/{id}", 999).headers(mockHttpHeaders())
                .content(objectMapper.writeValueAsString(
                        BookResourceIntegrationBody.bookUpdate())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors.[0]", is(String.format(BOOK_NOT_FOUND, 999))));
    }

    @Test
    @Order(10)
    public void whenUpdateBookWithCorrectIdReturnNoContent() throws Exception {
        mvc.perform(put("/books/{id}", bookId).headers(mockHttpHeaders())
                .content(objectMapper.writeValueAsString(
                        BookResourceIntegrationBody.bookUpdate())))
                .andExpect(status().isNoContent());

        Optional<Book> book = bookRepository.findById(Long.valueOf(bookId));

        assertTrue(book.isPresent());

        assertEquals(book.get().getTitle(), "Updated Test");
        assertEquals(book.get().getAuthor(), "updated test");
        assertEquals(book.get().getStatus(), BookStatusEnum.BORROWED);
    }

    @Test
    @Order(11)
    public void whenTryDeleteBookWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(delete("/books/{id}", 999).headers(mockHttpHeaders()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(NOT_FOUND_MSG)))
                .andExpect(jsonPath("errors.[0]", is(String.format(BOOK_NOT_FOUND, 999))));
    }

    @Test
    @Order(12)
    public void whenTryDeleteBookWithBorrowedStatusReturnBadRequest() throws Exception {
        mvc.perform(delete("/books/{id}", bookId).headers(mockHttpHeaders()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")))
                .andExpect(jsonPath("errors.[0]", is("Cannot delete a book with borrowed status")));
    }

    @Test
    @Order(13)
    public void whenDeleteBookWithCorrectIdReturnNoContent() throws Exception {
        Book book = bookRepository.findById(Long.valueOf(bookId)).get();
        book.setStatus(BookStatusEnum.AVAILABLE);
        bookRepository.save(book);

        mvc.perform(delete("/books/{id}", bookId).headers(mockHttpHeaders()))
                .andExpect(status().isNoContent());

        Optional<Book> deletedBook = bookRepository.findById(Long.valueOf(bookId));
        assertFalse(deletedBook.isPresent());
    }
}
