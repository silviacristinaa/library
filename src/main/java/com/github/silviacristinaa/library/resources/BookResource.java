package com.github.silviacristinaa.library.resources;

import com.github.silviacristinaa.library.dtos.requests.BookRequestDto;
import com.github.silviacristinaa.library.dtos.requests.BookStatusRequestDto;
import com.github.silviacristinaa.library.dtos.responses.BookResponseDto;
import com.github.silviacristinaa.library.exceptions.BadRequestException;
import com.github.silviacristinaa.library.exceptions.NotFoundException;
import com.github.silviacristinaa.library.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management service")
public class BookResource {

    private static final String ID = "/{id}";

    private final BookService bookService;

    @Operation(summary = "Get all")
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Page<BookResponseDto>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookService.findAll(pageable));
    }

    @Operation(summary = "Get by id")
    @GetMapping(value = ID)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<BookResponseDto> findById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(bookService.findOneBookById(id));
    }

    @Operation(summary = "Create")
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody @Valid BookRequestDto bookRequestDto) {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path(ID).buildAndExpand(bookService.create(bookRequestDto).getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Patch status")
    @PatchMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateBookStatus(
            @PathVariable Long id, @RequestBody BookStatusRequestDto bookStatusRequestDto) throws NotFoundException {
        bookService.updateBookStatus(id, bookStatusRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update")
    @PutMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid BookRequestDto bookRequestDto) throws NotFoundException {
        bookService.update(id, bookRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete")
    @DeleteMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException, BadRequestException {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
