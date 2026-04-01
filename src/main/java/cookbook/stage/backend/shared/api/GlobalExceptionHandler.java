package cookbook.stage.backend.shared.api;

import cookbook.stage.backend.recipe.shared.BaseQuantityInvalidException;
import cookbook.stage.backend.shared.domain.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseQuantityInvalidException.class)
    public ResponseEntity<ApiError>  handleBaseQuantityInvalidException(BaseQuantityInvalidException e) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Base quantity is invalid.",
                e.getMessage(),
                List.of(),
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError>  handleNotFoundException(NotFoundException e) {
        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                List.of(),
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError>  handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Not Found",
                e.getMessage(),
                List.of(),
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {
        ApiError body = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Something went wrong: " + ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
