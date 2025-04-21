package com.github.silviacristinaa.library.services.impl;

import com.github.silviacristinaa.library.dtos.requests.BookRequestDto;
import com.github.silviacristinaa.library.dtos.requests.BookStatusRequestDto;
import com.github.silviacristinaa.library.dtos.responses.BookResponseDto;
import com.github.silviacristinaa.library.entities.Book;
import com.github.silviacristinaa.library.enums.BookStatusEnum;
import com.github.silviacristinaa.library.exceptions.BadRequestException;
import com.github.silviacristinaa.library.exceptions.NotFoundException;
import com.github.silviacristinaa.library.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BookServiceImplTest {

    private static final String BOOK_NOT_FOUND = "Book %s not found";
    private static final String CANNOT_DELETE_BOOK_WITH_BORROWED_STATUS = "Cannot delete a book with borrowed status";

    private static final long ID = 1L;
    private static final String TITLE = "Test";
    private static final String AUTHOR = "test";
    private static final int INDEX = 0;

    private BookRequestDto bookRequestDto;
    private BookStatusRequestDto bookStatusRequestDto;
    private BookResponseDto bookResponseDto;

    private Book book;

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        bookRequestDto = new BookRequestDto(TITLE, AUTHOR);

        bookStatusRequestDto = new BookStatusRequestDto(BookStatusEnum.BORROWED);

        bookResponseDto = new BookResponseDto(ID, TITLE, AUTHOR, BookStatusEnum.AVAILABLE);

        book = new Book(ID, TITLE, AUTHOR, BookStatusEnum.AVAILABLE);
    }

    @Test
    void whenFindAllReturnBookResponseDtoPage() {
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(book)));
        when(modelMapper.map(any(), any())).thenReturn(bookResponseDto);

        Page<BookResponseDto> response = bookServiceImpl.findAll(Pageable.ofSize(1));

        assertNotNull(response);
        assertEquals(1, response.getSize());
        assertEquals(BookResponseDto.class, response.getContent().get(INDEX).getClass());

        assertEquals(ID, response.getContent().get(INDEX).getId());
        assertEquals(TITLE, response.getContent().get(INDEX).getTitle());
        assertEquals(AUTHOR, response.getContent().get(INDEX).getAuthor());
        assertEquals(BookStatusEnum.AVAILABLE, response.getContent().get(INDEX).getStatus());
    }

    @Test
    void whenFindByIdReturnOneBookResponseDto() throws NotFoundException {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(modelMapper.map(any(), any())).thenReturn(bookResponseDto);

        BookResponseDto response = bookServiceImpl.findOneBookById(ID);

        assertNotNull(response);

        assertEquals(BookResponseDto.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(TITLE, response.getTitle());
        assertEquals(AUTHOR, response.getAuthor());
        assertEquals(BookStatusEnum.AVAILABLE, response.getStatus());
    }

    @Test
    void whenTryFindByIdReturnNotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookServiceImpl.findOneBookById(ID));

        assertEquals(String.format(BOOK_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenCreateReturnSuccess() {
        when(modelMapper.map(any(), any())).thenReturn(book);
        when(bookRepository.save(any())).thenReturn(book);

        Book response = bookServiceImpl.create(bookRequestDto);

        assertNotNull(response);
        assertEquals(Book.class, response.getClass());
        assertEquals(ID, response.getId());
        assertEquals(TITLE, response.getTitle());
        assertEquals(AUTHOR, response.getAuthor());
        assertEquals(BookStatusEnum.AVAILABLE, response.getStatus());

        verify(bookRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenUpdateBookStatusReturnSuccess() throws NotFoundException {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        bookServiceImpl.updateBookStatus(ID, bookStatusRequestDto);

        verify(bookRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenTryUpdateBookStatusReturnNotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookServiceImpl.updateBookStatus(ID, bookStatusRequestDto));

        assertEquals(String.format(BOOK_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenUpdateReturnSuccess() throws NotFoundException {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(modelMapper.map(any(), any())).thenReturn(book);

        bookServiceImpl.update(ID, bookRequestDto);

        verify(bookRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenTryUpdateReturnNotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookServiceImpl.update(ID, bookRequestDto));

        assertEquals(String.format(BOOK_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenDeleteReturnSuccess() throws NotFoundException, BadRequestException {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        bookServiceImpl.delete(ID);

        verify(bookRepository, times(1)).delete(any());
    }

    @Test
    void whenTryDeleteReturnNotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookServiceImpl.delete(ID));

        assertEquals(String.format(BOOK_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenTryDeleteReturnBadRequestException() {
        book.setStatus(BookStatusEnum.BORROWED);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookServiceImpl.delete(ID));

        assertEquals(CANNOT_DELETE_BOOK_WITH_BORROWED_STATUS, exception.getMessage());
    }
}
