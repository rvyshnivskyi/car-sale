package com.playtika.sales.web;

import com.playtika.sales.exception.DuplicateCarSaleDetailsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CONFLICT;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingControllerAdvice {

    @ExceptionHandler(DuplicateCarSaleDetailsException.class)
    @ResponseStatus(CONFLICT)
    public String handleDuplicateCarSaleDetailsException(DuplicateCarSaleDetailsException e) {
        return processException("Duplicate car details exception: %s.", e);
    }

    private String processException(String messageFormat, Exception e) {
        String errorMessage = format(messageFormat, e.getMessage());
        log.error(errorMessage, e);
        return errorMessage;
    }
}
