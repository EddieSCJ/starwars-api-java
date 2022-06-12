package com.api.starwars.common.exceptions.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedError extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.UNAUTHORIZED.value();

    public UnauthorizedError(String message) {
        super(message);
    }
}
