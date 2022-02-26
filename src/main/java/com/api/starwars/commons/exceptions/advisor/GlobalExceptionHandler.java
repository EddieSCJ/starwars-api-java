package com.api.starwars.commons.exceptions.advisor;

import com.api.starwars.commons.exceptions.http.HttpBadRequestException;
import com.api.starwars.commons.exceptions.http.HttpNotFoundException;
import com.api.starwars.commons.exceptions.view.BadRequestExceptionResponseJson;
import com.api.starwars.commons.exceptions.view.BaseExceptionResponseJson;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.api.starwars.commons.helpers.MessageSourceHelper.getApiErrorMessage;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    @ApiResponse(responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseExceptionResponseJson.class)))
    public ResponseEntity<BaseExceptionResponseJson> handleGenericException() {
        BaseExceptionResponseJson response = new BaseExceptionResponseJson(HttpStatus.INTERNAL_SERVER_ERROR.value(), getApiErrorMessage("internal_server_error"));

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler({HttpBadRequestException.class})
    @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestExceptionResponseJson.class)))
    public ResponseEntity<BadRequestExceptionResponseJson> handleBadRequest(HttpBadRequestException exception) {
        BadRequestExceptionResponseJson response = new BadRequestExceptionResponseJson(HttpStatus.BAD_REQUEST.value(), getApiErrorMessage("bad_request"), exception.getErrors());

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler({HttpNotFoundException.class, NotFound.class})
    @ApiResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseExceptionResponseJson.class)))
    public ResponseEntity<BaseExceptionResponseJson> handleNotFoundException(Exception ex) {
        BaseExceptionResponseJson response = new BaseExceptionResponseJson(HttpNotFoundException.HTTP_STATUS_CODE, ex.getMessage());

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    @ApiResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseExceptionResponseJson.class)))
    public ResponseEntity<BaseExceptionResponseJson> handleNoHandlerFoundException() {
        BaseExceptionResponseJson response = new BaseExceptionResponseJson(HttpStatus.NOT_FOUND.value(), getApiErrorMessage("resource_not_found"));

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler({MethodNotAllowedException.class, HttpRequestMethodNotSupportedException.class})
    @ApiResponse(responseCode = "405", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseExceptionResponseJson.class)))
    public ResponseEntity<BaseExceptionResponseJson> handleMethodNotAllowedException() {
        BaseExceptionResponseJson response = new BaseExceptionResponseJson(HttpStatus.METHOD_NOT_ALLOWED.value(), getApiErrorMessage("method_not_allowed"));

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

}
