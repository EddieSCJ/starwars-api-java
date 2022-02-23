package com.api.starwars.commons.exceptions.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class HttpInternalServerErrorException extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public HttpInternalServerErrorException(String message) {
        super(message);
    }
}
