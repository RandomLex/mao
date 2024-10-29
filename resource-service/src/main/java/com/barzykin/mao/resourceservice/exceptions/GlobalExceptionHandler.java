package com.barzykin.mao.resourceservice.exceptions;

import com.barzykin.mao.resourceservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An internal server error occurred";

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFound(ResourceNotFoundException e) {
        log.debug(e.getClass().getName());
        log.debug(HttpStatus.NOT_FOUND.getReasonPhrase(), e); // debug level as soon as NotFoundException is a common case
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
            )
        ));
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ErrorResponse> handleSongNotFoundException(UnsupportedMediaTypeStatusException e) {
        log.error(e.getClass().getName());
        String reason = e.getBody().getDetail();
        log.error("Unsupported MediaType: {}", reason);

        return new ResponseEntity<>(
            new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                reason
            ),
            HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleSongNotFoundException(HandlerMethodValidationException e) {
        log.error(e.getClass().getName());
        log.error("Length of 'id' parameter is more that 200: {}", e.getMessage());

        return new ResponseEntity<>(
            new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage()
            ),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidFileException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidFileException(InvalidFileException e) {
        log.debug(e.getClass().getName());
        log.debug( HttpStatus.BAD_REQUEST.getReasonPhrase(), e);
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
