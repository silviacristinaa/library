package com.github.silviacristinaa.library.exceptions;

public class NotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotFoundException(final String error) {
        super(error);
    }
}
