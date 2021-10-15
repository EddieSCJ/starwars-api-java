package com.api.starwars.response;

import com.api.commons.response.HttpResponses;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response<T> {
    @JsonProperty("erros")
    List<String> errors = new ArrayList<>();
    @JsonProperty("resultado")
    T result = null;
    Integer status = 200;


    public static <T> Response<T> getValidatedResponseInstance(
            Boolean alreadyExists,
            Boolean shouldExists,
            BindingResult result
    ) {
        Response<T> response = new Response<>();

        if (!shouldExists && alreadyExists) {
            response.getErrors().add(HttpResponses.ALREADY_EXISTS.getDescription());
            response.setStatus(HttpResponses.ALREADY_EXISTS.getCode());

        } else if (shouldExists && !alreadyExists) {
            response.getErrors().add(HttpResponses.NOT_FOUND.getDescription());
            response.setStatus(HttpResponses.NOT_FOUND.getCode());
        }
        if (result != null && result.hasErrors()) {
            response.getErrors().add(HttpResponses.BAD_REQUEST.getDescription());
            response.setStatus(HttpResponses.BAD_REQUEST.getCode());
        }

        return response;
    }
}
