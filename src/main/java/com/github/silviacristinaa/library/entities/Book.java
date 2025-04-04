package com.github.silviacristinaa.library.entities;

import com.github.silviacristinaa.library.enums.BookStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(nullable = false, length = 255)
    private String author;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatusEnum status;
}
