package com.barzykin.mao.songservice.errors;

import com.barzykin.mao.songservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An internal server error occurred";

    @ExceptionHandler(SongNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSongNotFoundException(SongNotFoundException e) {
        log.error(e.getClass().getName());
        log.error("Song not found: {}", e.getMessage());

        return new ResponseEntity<>(
            new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(WebExchangeBindException e) {
        log.error(e.getClass().getName());
        log.error("Validation failed for the request: {}", e.getBindingResult().getTarget());
        e.getBindingResult().getFieldErrors()
            .forEach(error ->
                log.error("Field: {}, Error: {}",
                    error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(
            new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                spliceErrorMessage(e)
            ),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error(e.getClass().getName());
        log.error(INTERNAL_SERVER_ERROR_MESSAGE, e);

        return new ResponseEntity<>(
            new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                INTERNAL_SERVER_ERROR_MESSAGE
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private static String spliceErrorMessage(WebExchangeBindException e) {
        return e.getBindingResult()
            .getAllErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));
    }
}
