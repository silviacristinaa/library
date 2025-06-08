package com.github.silviacristinaa.library.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class LoanRequestDto {

    @NotNull
    private Long bookId;
    @NotNull
    private Long studentId;
}
