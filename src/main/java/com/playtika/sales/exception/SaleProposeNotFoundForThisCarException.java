package com.playtika.sales.exception;

public class SaleProposeNotFoundForThisCarException extends RuntimeException {
    public SaleProposeNotFoundForThisCarException(long carId) {
        super("There aren't any open sale proposes for car with id = [" + carId + "]");
    }
}
