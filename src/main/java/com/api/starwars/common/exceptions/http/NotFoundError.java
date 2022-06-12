package com.api.starwars.common.exceptions.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundError extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.NOT_FOUND.value();

    public NotFoundError(String message) { super(message); }
}
