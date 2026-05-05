package be.xplore.cookbook.web.exception;

import be.xplore.cookbook.ai.exception.AiConnectionException;
import be.xplore.cookbook.ai.exception.AiInvalidResponseException;
import be.xplore.cookbook.core.domain.exception.DataIntegrityException;
import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.security.exception.OAuth2Exception;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({
            OAuth2Exception.class,
            UserNotFoundException.class
    })
    ResponseEntity<Object> handleUnauthorizedException(RuntimeException ex, WebRequest request) {
        String responseBody = ex.getMessage();
        return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        String responseBody = ex.getMessage();
        return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            DataIntegrityException.class
    })
    ResponseEntity<Object> handleBadRequestExceptions(RuntimeException ex, WebRequest request) {
        String responseBody = ex.getMessage();
        return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String responseBody = ex.getMessage();
        return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(AiInvalidResponseException.class)
    ResponseEntity<Object> handleBadGatewayException(AiInvalidResponseException ex, WebRequest request) {
        String responseBody = ex.getMessage();
        return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.BAD_GATEWAY, request);
    }

    @ExceptionHandler(AiConnectionException.class)
    ResponseEntity<Object> handleServiceUnavailableException(AiConnectionException ex, WebRequest request) {
        String responseBody = ex.getMessage();
        return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(),
                HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        String responseBody = "An unexpected internal server error occurred: " + ex.getMessage();
        return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
