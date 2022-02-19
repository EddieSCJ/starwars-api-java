package com.api.starwars.commons.errors.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ErrorControllerHandler implements ErrorController {

    @RequestMapping("/resource-error")
    public String handleError(HttpServletRequest request) throws Exception {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                throw new NoHandlerFoundException(request.getMethod(), request.getRequestURI(), HttpHeaders.EMPTY);
            }
        }

        throw new Exception();
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
