package com.github.silviacristinaa.library.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends Exception {

    private static final long serialVersionUID = 1L;

    public InternalServerErrorException(final String error) {
        super(error);
    }
}
