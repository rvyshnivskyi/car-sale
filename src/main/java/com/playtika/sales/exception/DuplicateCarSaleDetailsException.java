package com.playtika.sales.exception;

import com.playtika.sales.domain.Car;

import static java.lang.String.format;

public class DuplicateCarSaleDetailsException extends RuntimeException {
    public DuplicateCarSaleDetailsException(Car car, Throwable ex) {
        super(format("Car with details=[%s] can't be added, cause car with duplicate os some details is already exist",
                car.toString()), ex);
    }
}
