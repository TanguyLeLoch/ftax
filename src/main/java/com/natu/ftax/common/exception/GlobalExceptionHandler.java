package com.natu.ftax.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        ExceptionResponse response = new ExceptionResponse(exception.getMessage());
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException exception) {
        ExceptionResponse response = new ExceptionResponse(exception.getMessage());
        LOGGER.info(exception.getMessage(), exception);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(NotFoundException exception) {
        ExceptionResponse response = new ExceptionResponse(exception.getMessage());
        LOGGER.info(exception.getMessage(), exception);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FunctionalException.class)
    public ResponseEntity<ExceptionResponse> handleFunctionalException(FunctionalException exception) {
        ExceptionResponse response = new ExceptionResponse(exception.getMessage());
        LOGGER.info(exception.getMessage(), exception);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ExceptionResponse> handleTechnicalException(TechnicalException exception) {
        ExceptionResponse response = new ExceptionResponse(exception.getMessage());
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        ExceptionResponse response = new ExceptionResponse(exception.getMessage());
        LOGGER.info(exception.getMessage(), exception);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException exception) {
        ExceptionResponse response = new ExceptionResponse(exception.getMessage());
        LOGGER.info(exception.getMessage(), exception);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
