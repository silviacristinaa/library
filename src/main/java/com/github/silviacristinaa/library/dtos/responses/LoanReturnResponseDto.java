package com.github.silviacristinaa.library.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanReturnResponseDto {

    private String message;
    private LoanResponseDto loanResponseDto;
}
