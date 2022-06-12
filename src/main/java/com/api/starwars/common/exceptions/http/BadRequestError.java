package com.api.starwars.common.exceptions.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class BadRequestError extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.NOT_FOUND.value();
    private final List<String> errors;

    public BadRequestError(List<String> errors) {
        this.errors = errors;
    }
}
