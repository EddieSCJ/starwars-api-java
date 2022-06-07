package com.api.starwars.common.exceptions.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class HttpBadRequestException extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.NOT_FOUND.value();
    private final List<String> errors;

    public HttpBadRequestException(List<String> errors) {
        this.errors = errors;
    }
}
