package com.api.starwars.common.exceptions.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerError extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public InternalServerError(String message) {
        super(message);
    }
}
