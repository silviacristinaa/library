package com.github.silviacristinaa.library.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class BookRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String author;
}
