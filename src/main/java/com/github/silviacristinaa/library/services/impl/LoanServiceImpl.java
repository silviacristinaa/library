package com.github.silviacristinaa.library.services.impl;

import com.github.silviacristinaa.library.clients.StudentClient;
import com.github.silviacristinaa.library.dtos.requests.BookStatusRequestDto;
import com.github.silviacristinaa.library.dtos.requests.LoanRequestDto;
import com.github.silviacristinaa.library.dtos.requests.LoanStatusRequestDto;
import com.github.silviacristinaa.library.dtos.responses.LoanResponseDto;
import com.github.silviacristinaa.library.dtos.responses.LoanReturnResponseDto;
import com.github.silviacristinaa.library.dtos.responses.StudentResponseDto;
import com.github.silviacristinaa.library.entities.Book;
import com.github.silviacristinaa.library.entities.Loan;
import com.github.silviacristinaa.library.enums.BookStatusEnum;
import com.github.silviacristinaa.library.enums.LoanStatusEnum;
import com.github.silviacristinaa.library.exceptions.BadRequestException;
import com.github.silviacristinaa.library.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.library.exceptions.NotFoundException;
import com.github.silviacristinaa.library.repositories.LoanRepository;
import com.github.silviacristinaa.library.services.BookService;
import com.github.silviacristinaa.library.services.LoanService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoanServiceImpl implements LoanService {

    private static final String LOAN_NOT_FOUND = "Loan %s not found";
    private static final String STUDENT_NOT_FOUND = "Student %s not found";
    private static final String STUDENT_IS_INACTIVE = "Student %s is inactive";
    public static final String BOOK_ALREADY_ON_LOAN = "This book is currently on loan.";
    private static final int DEFAULT_LOAN_DAYS = 7;
    private static final BigDecimal DAILY_FINE_AMOUNT = BigDecimal.valueOf(2.0);
    private static final String PROBLEM_STUDENTS_EXTERNAL_API = "There was a problem consuming the students external api";

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final StudentClient studentClient;
    private final ModelMapper modelMapper;

    @Override
    public Page<LoanResponseDto> findAll(Pageable pageable) {
        return loanRepository.findAll(pageable).map(loan -> modelMapper.map(loan, LoanResponseDto.class));
    }

    @Override
    public LoanResponseDto findOneLoanById(Long id) throws NotFoundException {
        Loan loan = findById(id);
        return modelMapper.map(loan, LoanResponseDto.class);
    }

    @Override
    @Transactional
    public Loan create(LoanRequestDto loanRequestDto) throws NotFoundException, BadRequestException,
            InternalServerErrorException {

        Book book = validateAvailableBook(loanRequestDto.getBookId());
        validateActiveStudent(loanRequestDto.getStudentId());

        LocalDate today = LocalDate.now();

        book.setStatus(BookStatusEnum.BORROWED);
        bookService.save(book);

        Loan loan = buildLoan(book, loanRequestDto.getStudentId(), today);

        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public void updateLoanStatus(Long id, LoanStatusRequestDto loanStatusRequestDto) throws NotFoundException {
        Loan loan = findById(id);

        loan.setStatus(loanStatusRequestDto.getStatus());
        loanRepository.save(loan);
    }

    @Override
    @Transactional
    public void update(Long id, LoanRequestDto loanRequestDto) throws NotFoundException {
        Loan loan = findById(id);

        loan.setBook(modelMapper.map(bookService.findOneBookById(loanRequestDto.getBookId()), Book.class));
        loan.setStudentId(loanRequestDto.getStudentId());

        loanRepository.save(loan);
    }

    @Override
    @Transactional
    public void delete(Long id) throws NotFoundException {
        Loan loan = findById(id);

        loanRepository.delete(loan);
    }

    @Override
    @Transactional
    public LoanReturnResponseDto returnedLoan(Long id) throws NotFoundException {
        Loan loan = findById(id);

        LocalDate today = LocalDate.now();
        Long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), today);

        BigDecimal fineAmount = calculateFine(daysLate);

        loan.setFineAmount(fineAmount);
        loan.setReturnDate(today);
        loan.setStatus(LoanStatusEnum.RETURNED);

        bookService.updateBookStatus(loan.getBook().getId(), new BookStatusRequestDto(BookStatusEnum.AVAILABLE));

        loanRepository.save(loan);

        LoanReturnResponseDto responseDto = new LoanReturnResponseDto();
        responseDto.setLoanResponseDto(modelMapper.map(loan, LoanResponseDto.class));
        responseDto.setMessage(buildReturnMessage(daysLate, fineAmount));

        return responseDto;
    }

    private Loan findById(Long id) throws NotFoundException {
        return loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(LOAN_NOT_FOUND, id)));
    }

    private Book validateAvailableBook(Long bookId) throws NotFoundException, BadRequestException {
        Book book = bookService.findById(bookId);
        if (BookStatusEnum.BORROWED.equals(book.getStatus())) {
            throw new BadRequestException(BOOK_ALREADY_ON_LOAN);
        }
        return book;
    }

    private Loan buildLoan(Book book, Long studentId, LocalDate today) {
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setStudentId(studentId);
        loan.setLoanDate(today);
        loan.setDueDate(today.plusDays(DEFAULT_LOAN_DAYS));
        loan.setStatus(LoanStatusEnum.ON_LOAN);
        return loan;
    }

    private BigDecimal calculateFine(Long daysLate) {
        if (daysLate > 0) {
            return BigDecimal.valueOf(daysLate).multiply(DAILY_FINE_AMOUNT);
        }
        return BigDecimal.ZERO;
    }

    private String buildReturnMessage(Long daysLate, BigDecimal fineAmount) {
        if (fineAmount.compareTo(BigDecimal.ZERO) == 0) {
            return "Livro devolvido no prazo. Nenhuma multa foi aplicada.";
        }

        String formattedFine = "R$ " + fineAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();
        return String.format("Livro devolvido com %d dia%s de atraso. Multa aplicada: %s.",
                daysLate,
                daysLate == 1 ? "" : "s",
                formattedFine);
    }

    private void validateActiveStudent(Long studentId)
            throws NotFoundException, InternalServerErrorException, BadRequestException {

        StudentResponseDto student = null;

        try {
            StudentResponseDto studentResponseDto = studentClient.findById(studentId);

            student = studentResponseDto;
        } catch (FeignException ex) {
            if (HttpStatus.NOT_FOUND.value() == ex.status()) {
                throw new NotFoundException(String.format(STUDENT_NOT_FOUND, studentId));
            }
        } catch (Exception ex) {
            log.error(PROBLEM_STUDENTS_EXTERNAL_API, ex);
            throw new InternalServerErrorException(PROBLEM_STUDENTS_EXTERNAL_API);
        }
        if (!student.isActive()) {
            throw new BadRequestException(String.format(STUDENT_IS_INACTIVE, studentId));
        }
    }
}
