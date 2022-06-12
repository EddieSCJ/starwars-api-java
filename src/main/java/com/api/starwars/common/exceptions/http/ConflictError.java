package com.api.starwars.common.exceptions.http;

import org.springframework.http.HttpStatus;

public class ConflictError extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.CONFLICT.value();

    public ConflictError(String message) { super(message); }
}
