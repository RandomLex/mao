package com.barzykin.mao.resourceservice.exceptions;

import com.barzykin.mao.resourceservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String BAD_REQUEST_MESSAGE = "Bad request";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An internal server error occurred";

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFound(ResourceNotFoundException e) {
        log.debug(e.getClass().getName());
        log.debug(BAD_REQUEST_MESSAGE, e);
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
            )
        ));
    }

    @ExceptionHandler(InvalidFileException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidFileException(InvalidFileException e) {
        log.debug(e.getClass().getName());
        log.debug(BAD_REQUEST_MESSAGE, e);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage()
            )
        ));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception e) {
        log.error(e.getClass().getName());
        log.error(INTERNAL_SERVER_ERROR_MESSAGE, e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                INTERNAL_SERVER_ERROR_MESSAGE
            )
        ));
    }
}
