package com.api.starwars.common.exceptions.advisor;

import com.api.starwars.common.exceptions.http.BadRequestError;
import com.api.starwars.common.exceptions.http.ConflictError;
import com.api.starwars.common.exceptions.http.NotFoundError;
import com.api.starwars.common.exceptions.http.UnauthorizedError;
import com.api.starwars.common.exceptions.view.BadRequestExceptionResponseJson;
import com.api.starwars.common.exceptions.view.BaseExceptionResponseJson;
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

import static com.api.starwars.infra.helpers.MessageSourceHelper.getApiErrorMessage;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    @ApiResponse(responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseExceptionResponseJson.class)))
    public ResponseEntity<BaseExceptionResponseJson> handleGenericException(Exception ex) {
        BaseExceptionResponseJson response = new BaseExceptionResponseJson(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler({BadRequestError.class})
    @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestExceptionResponseJson.class)))
    public ResponseEntity<BadRequestExceptionResponseJson> handleBadRequest(BadRequestError exception) {
        BadRequestExceptionResponseJson response = new BadRequestExceptionResponseJson(HttpStatus.BAD_REQUEST.value(), getApiErrorMessage("bad_request"), exception.getErrors());

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler({UnauthorizedError.class,
//            AccessDeniedException.class
    })
    @ApiResponse(responseCode = "401", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseExceptionResponseJson.class)))
    public ResponseEntity<BaseExceptionResponseJson> handleUnauthorizedException(Exception ex) {
        BaseExceptionResponseJson response = new BaseExceptionResponseJson(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler({NotFoundError.class, NotFound.class})
    @ApiResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseExceptionResponseJson.class)))
    public ResponseEntity<BaseExceptionResponseJson> handleNotFoundException(Exception ex) {
        BaseExceptionResponseJson response = new BaseExceptionResponseJson(NotFoundError.HTTP_STATUS_CODE, ex.getMessage());

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

    @ExceptionHandler({ConflictError.class})
    @ApiResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseExceptionResponseJson.class)))
    public ResponseEntity<BaseExceptionResponseJson> handleConflictException(ConflictError ex) {
        BaseExceptionResponseJson response = new BaseExceptionResponseJson(ConflictError.HTTP_STATUS_CODE, ex.getMessage());

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

}
