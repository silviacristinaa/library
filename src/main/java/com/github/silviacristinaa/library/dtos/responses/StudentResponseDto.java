package com.github.silviacristinaa.library.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class StudentResponseDto {

    private Long id;
    private String name;
    private String email;
    private boolean active;
}
