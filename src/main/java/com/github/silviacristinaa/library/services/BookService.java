package com.github.silviacristinaa.library.services;

import com.github.silviacristinaa.library.dtos.requests.BookRequestDto;
import com.github.silviacristinaa.library.dtos.requests.BookStatusRequestDto;
import com.github.silviacristinaa.library.dtos.responses.BookResponseDto;
import com.github.silviacristinaa.library.entities.Book;
import com.github.silviacristinaa.library.exceptions.BadRequestException;
import com.github.silviacristinaa.library.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    Page<BookResponseDto> findAll(Pageable pageable);

    BookResponseDto findOneBookById(Long id) throws NotFoundException;

    Book create(BookRequestDto bookRequestDto);

    void updateBookStatus(Long id, BookStatusRequestDto bookStatusRequestDto) throws NotFoundException;

    void update(Long id, BookRequestDto bookRequestDto) throws NotFoundException;

    void delete(Long id) throws NotFoundException, BadRequestException;

    Book findById(Long id) throws NotFoundException;

    Book save(Book book);
}
