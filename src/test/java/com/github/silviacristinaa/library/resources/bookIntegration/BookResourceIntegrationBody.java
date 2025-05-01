package com.github.silviacristinaa.library.resources.bookIntegration;

import com.github.silviacristinaa.library.dtos.requests.BookRequestDto;
import com.github.silviacristinaa.library.dtos.requests.BookStatusRequestDto;
import com.github.silviacristinaa.library.enums.BookStatusEnum;

public class BookResourceIntegrationBody {

    public static BookRequestDto bookException() {
        return new BookRequestDto(null, "test");
    }

    public static BookRequestDto bookCreate() {
        return new BookRequestDto("Test", "test");
    }

    public static BookRequestDto bookUpdate() {
        return new BookRequestDto("Updated Test", "updated test");
    }

    public static BookStatusRequestDto updateBookStatus() {
        return new BookStatusRequestDto(BookStatusEnum.BORROWED);
    }
}
