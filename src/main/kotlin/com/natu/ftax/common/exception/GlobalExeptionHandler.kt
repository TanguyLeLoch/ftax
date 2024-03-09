package com.natu.ftax.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleResourceNotFoundException(exception: NotFoundException): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(exception.message!!)
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

}