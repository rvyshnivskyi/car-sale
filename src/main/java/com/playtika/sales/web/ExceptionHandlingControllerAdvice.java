package com.playtika.sales.web;

import com.playtika.sales.exception.ActiveOfferWithThisIdWasNotFoundException;
import com.playtika.sales.exception.CarWasNotFoundException;
import com.playtika.sales.exception.DuplicateCarSaleDetailsException;
import com.playtika.sales.exception.SaleProposeNotFoundForThisCarException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingControllerAdvice {

    @ExceptionHandler(DuplicateCarSaleDetailsException.class)
    @ResponseStatus(CONFLICT)
    public String handleDuplicateCarSaleDetailsException(DuplicateCarSaleDetailsException e) {
        return processException("Duplicate car details exception: %s.", e);
    }

    @ExceptionHandler(SaleProposeNotFoundForThisCarException.class)
    @ResponseStatus(NOT_FOUND)
    public String handleSaleProposeNotFoundForThisCarException(SaleProposeNotFoundForThisCarException e) {
        return processException("Sale proposition was not found exception: %s.", e);
    }

    @ExceptionHandler(ActiveOfferWithThisIdWasNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public String handleActiveOfferWithThisIdWasNotFoundException(ActiveOfferWithThisIdWasNotFoundException e) {
        return processException("Active offer with inserted id was not found exception: %s.", e);
    }

    @ExceptionHandler(CarWasNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public String handleCarWasNotFoundException(CarWasNotFoundException e) {
        return processException("Car with inserted id was not found exception: %s.", e);
    }

    private String processException(String messageFormat, Exception e) {
        String errorMessage = format(messageFormat, e.getMessage());
        log.error(errorMessage, e);
        return errorMessage;
    }
}
