package com.api.starwars.helpers;

import com.api.starwars.domain.enums.HttpResponses;
import com.api.starwars.response.Response;
import org.springframework.validation.BindingResult;

public class ApiHelper {

    public final static String API = "/api";
    public final static String PLANET = "/planets";
    public final static String PAGINATED = "/paginated";
    public final static String ID = "/id/{id}";
    public final static String NAME = "/nome/{name}";

    public static <T> Response<T> getValidatedResponseInstance(
            Boolean alreadyExists,
            Boolean shouldExists,
            BindingResult result
    ) {
        Response<T> response = new Response<T>();

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
