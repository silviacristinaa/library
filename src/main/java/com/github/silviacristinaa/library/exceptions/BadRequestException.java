package com.github.silviacristinaa.library.exceptions;

public class BadRequestException extends Exception {

    private static final long serialVersionUID = 1L;

    public BadRequestException(final String error) {
        super(error);
    }
}
