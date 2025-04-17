package com.github.silviacristinaa.library.services.impl;

import com.github.silviacristinaa.library.dtos.requests.BookRequestDto;
import com.github.silviacristinaa.library.dtos.requests.BookStatusRequestDto;
import com.github.silviacristinaa.library.dtos.responses.BookResponseDto;
import com.github.silviacristinaa.library.entities.Book;
import com.github.silviacristinaa.library.enums.BookStatusEnum;
import com.github.silviacristinaa.library.exceptions.NotFoundException;
import com.github.silviacristinaa.library.repositories.BookRepository;
import com.github.silviacristinaa.library.services.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private static final String CANNOT_DELETE_BOOK_WITH_BORROWED_STATUS = "Cannot delete a book with borrowed status";
    private static final String BOOK_NOT_FOUND = "Book %s not found";

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<BookResponseDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(book -> modelMapper.map(book, BookResponseDto.class));
    }

    @Override
    public BookResponseDto findOneBookById(Long id) throws NotFoundException {
        Book book = findById(id);
        return modelMapper.map(book, BookResponseDto.class);
    }

    @Override
    @Transactional
    public Book create(BookRequestDto bookRequestDto) {
        Book book = modelMapper.map(bookRequestDto, Book.class);
        book.setStatus(BookStatusEnum.AVAILABLE);

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void updateBookStatus(Long id, BookStatusRequestDto bookStatusRequestDto) throws NotFoundException {
        Book book = findById(id);

        book.setStatus(bookStatusRequestDto.getStatus());
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void update(Long id, BookRequestDto bookRequestDto) throws NotFoundException {
        Book book = findById(id);

        book.setTitle(bookRequestDto.getTitle());
        book.setAuthor(bookRequestDto.getAuthor());

        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void delete(Long id) throws NotFoundException {
        Book book = findById(id);

        if (book.getStatus() == BookStatusEnum.BORROWED) {
            throw new IllegalStateException(CANNOT_DELETE_BOOK_WITH_BORROWED_STATUS);
        }

        bookRepository.delete(book);
    }

    private Book findById(Long id) throws NotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(BOOK_NOT_FOUND, id)));
    }
}
