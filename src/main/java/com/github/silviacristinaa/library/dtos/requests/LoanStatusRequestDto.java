package com.github.silviacristinaa.library.dtos.requests;

import com.github.silviacristinaa.library.enums.LoanStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanStatusRequestDto {

    @NotNull
    private LoanStatusEnum status;
}
