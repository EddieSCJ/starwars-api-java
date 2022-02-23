package com.api.starwars.commons.exceptions.view;

import lombok.Getter;

@Getter
public class BaseExceptionResponseJson {
    private final Integer httpStatusCode;
    private final String message;

    public BaseExceptionResponseJson(Integer httpStatusCode, String message) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
