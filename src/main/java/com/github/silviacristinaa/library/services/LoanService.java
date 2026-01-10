package com.github.silviacristinaa.library.services;

import com.github.silviacristinaa.library.dtos.requests.LoanRequestDto;
import com.github.silviacristinaa.library.dtos.requests.LoanStatusRequestDto;
import com.github.silviacristinaa.library.dtos.responses.LoanResponseDto;
import com.github.silviacristinaa.library.dtos.responses.LoanReturnResponseDto;
import com.github.silviacristinaa.library.entities.Loan;
import com.github.silviacristinaa.library.exceptions.BadRequestException;
import com.github.silviacristinaa.library.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.library.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanService {

    Page<LoanResponseDto> findAll(Pageable pageable);

    LoanResponseDto findOneLoanById(Long id) throws NotFoundException;

    Loan create(LoanRequestDto loanRequestDto) throws NotFoundException, BadRequestException, InternalServerErrorException;

    void updateLoanStatus(Long id, LoanStatusRequestDto loanStatusRequestDto) throws NotFoundException;

    void update(Long id, LoanRequestDto loanRequestDto) throws NotFoundException;

    void delete(Long id) throws NotFoundException;

    LoanReturnResponseDto returnedLoan(Long id) throws NotFoundException;
}
