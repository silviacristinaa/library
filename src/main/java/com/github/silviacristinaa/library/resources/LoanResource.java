package com.github.silviacristinaa.library.resources;

import com.github.silviacristinaa.library.dtos.requests.LoanRequestDto;
import com.github.silviacristinaa.library.dtos.requests.LoanStatusRequestDto;
import com.github.silviacristinaa.library.dtos.responses.LoanResponseDto;
import com.github.silviacristinaa.library.dtos.responses.LoanReturnResponseDto;
import com.github.silviacristinaa.library.exceptions.BadRequestException;
import com.github.silviacristinaa.library.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.library.exceptions.NotFoundException;
import com.github.silviacristinaa.library.services.LoanService;
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
@RequestMapping(value = "/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan management service")
public class LoanResource {

    private static final String ID = "/{id}";
    public static final String ID_RETURN = ID + "/return";

    private final LoanService loanService;

    @Operation(summary = "Get all")
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Page<LoanResponseDto>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(loanService.findAll(pageable));
    }

    @Operation(summary = "Get by ID")
    @GetMapping(value = ID)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<LoanResponseDto> findById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(loanService.findOneLoanById(id));
    }

    @Operation(summary = "Create")
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody @Valid LoanRequestDto loanRequestDto) throws NotFoundException,
            BadRequestException, InternalServerErrorException {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path(ID).buildAndExpand(loanService.create(loanRequestDto).getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Patch status")
    @PatchMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateLoanStatus(
            @PathVariable Long id, @RequestBody LoanStatusRequestDto loanStatusRequestDto) throws NotFoundException {
        loanService.updateLoanStatus(id, loanStatusRequestDto);
        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Update")
    @PutMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid LoanRequestDto loanRequestDto) throws NotFoundException {
        loanService.update(id, loanRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete")
    @DeleteMapping(value = ID)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
        loanService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Register the return of a loan by ID")
    @PutMapping(value = ID_RETURN)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<LoanReturnResponseDto> returnedLoan(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(loanService.returnedLoan(id));
    }
}
