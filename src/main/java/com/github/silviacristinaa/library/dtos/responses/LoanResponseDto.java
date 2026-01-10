package com.github.silviacristinaa.library.dtos.responses;

import com.github.silviacristinaa.library.enums.LoanStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class LoanResponseDto {

    private Long id;
    private BookResponseDto book;
    private Long studentId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatusEnum status;
    private BigDecimal fineAmount;
}
