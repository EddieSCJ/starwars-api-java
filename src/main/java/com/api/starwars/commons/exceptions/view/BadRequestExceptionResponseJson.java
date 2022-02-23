package com.api.starwars.commons.exceptions.view;

import lombok.Getter;

import java.util.List;

@Getter
public class BadRequestExceptionResponseJson extends BaseExceptionResponseJson {
    private final List<String> errors;

    public BadRequestExceptionResponseJson(Integer httpStatusCode, String message, List<String> errors) {
        super(httpStatusCode, message);
        this.errors = errors;
    }
}
