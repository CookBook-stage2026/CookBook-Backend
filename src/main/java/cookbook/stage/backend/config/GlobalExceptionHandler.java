package cookbook.stage.backend.config;

import cookbook.stage.backend.domain.exception.DataIntegrityException;
import cookbook.stage.backend.domain.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
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

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        String responseBody = "An unexpected internal server error occurred: " + ex.getMessage();
        return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
