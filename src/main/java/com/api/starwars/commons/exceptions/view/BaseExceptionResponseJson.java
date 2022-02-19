package com.api.starwars.commons.exceptions.view;

import lombok.Data;

@Data
public class BaseExceptionResponseJson {
    private final Integer httpStatusCode;
    private final String message;

    public BaseExceptionResponseJson(Integer httpStatusCode, String message) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
