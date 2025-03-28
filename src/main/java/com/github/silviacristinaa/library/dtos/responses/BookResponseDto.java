package com.github.silviacristinaa.library.dtos.responses;

import com.github.silviacristinaa.library.enums.BookStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class BookResponseDto {

    private Long id;
    private String title;
    private String author;
    private BookStatusEnum status;
}
