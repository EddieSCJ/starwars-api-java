package com.api.starwars.common.errors.handler;

import com.api.starwars.common.exceptions.http.HttpInternalServerErrorException;
import com.api.starwars.infra.helpers.MessageSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ErrorControllerHandler implements ErrorController {

    @RequestMapping(path = "/resource-error",  method = {RequestMethod.GET, RequestMethod.POST})
    public String handleError(HttpServletRequest request) throws NoHandlerFoundException, HttpInternalServerErrorException {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                throw new NoHandlerFoundException(request.getMethod(), request.getRequestURI(), HttpHeaders.EMPTY);
            }
        }

        throw new HttpInternalServerErrorException(MessageSourceHelper.getApiErrorMessage("internal_server_error"));
    }
}
