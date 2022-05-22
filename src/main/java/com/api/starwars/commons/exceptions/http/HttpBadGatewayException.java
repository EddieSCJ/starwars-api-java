package com.api.starwars.commons.exceptions.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class HttpBadGatewayException extends RuntimeException {
    public static final Integer HTTP_STATUS_CODE = HttpStatus.BAD_GATEWAY.value();
    private final List<String> errors;

    public HttpBadGatewayException(List<String> errors) {
        this.errors = errors;
    }
}
