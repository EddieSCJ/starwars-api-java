package com.api.starwars.commons.exceptions.view;

import lombok.Getter;

@Getter
public class BaseExceptionResponseJson {
    private final Integer HTTP_STATUS_CODE;
    private final String message;

    public BaseExceptionResponseJson(Integer httpStatusCode, String message) {
        this.message = message;
        this.HTTP_STATUS_CODE = httpStatusCode;
    }
}
