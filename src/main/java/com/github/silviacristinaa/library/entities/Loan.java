package com.github.silviacristinaa.library.entities;

import com.github.silviacristinaa.library.enums.LoanStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Column(name = "return_date")
    private LocalDate returnDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatusEnum status;
    @Column(name = "fine_amount")
    private BigDecimal fineAmount;
}
