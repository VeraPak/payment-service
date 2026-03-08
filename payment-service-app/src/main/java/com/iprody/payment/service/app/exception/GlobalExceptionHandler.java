package com.iprody.payment.service.app.exception;

import com.iprody.payment.service.app.exception.errorhandle.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException ex) {
        return new ErrorResponseDto(ex.getMessage(), ex.getOperation(), ex.getEntityId());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleOther(Exception ex) {
        return new ErrorResponseDto(
            ex.getMessage(),
            null,
            null
        );
    }
}