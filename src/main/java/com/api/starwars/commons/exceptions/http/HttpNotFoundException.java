package com.api.starwars.commons.exceptions.http;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class HttpNotFoundException extends RuntimeException {
    public static final Integer httpStatusCode = HttpStatus.NOT_FOUND.value();
    private final String message;

    public HttpNotFoundException(String message) {
        this.message = message;
    }
}
