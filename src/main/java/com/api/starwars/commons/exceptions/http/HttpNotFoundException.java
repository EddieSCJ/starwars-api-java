package com.api.starwars.commons.exceptions.http;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpNotFoundException extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.NOT_FOUND.value();
    private final String message;

    public HttpNotFoundException(String message) {
        this.message = message;
    }
}
