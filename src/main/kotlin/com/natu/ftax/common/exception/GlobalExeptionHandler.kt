package com.natu.ftax.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleResourceNotFoundException(exception: NotFoundException): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(exception.message!!)
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FunctionalException::class)
    fun handleFunctionalException(exception: FunctionalException): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(exception.message!!)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(exception.message!!)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

}