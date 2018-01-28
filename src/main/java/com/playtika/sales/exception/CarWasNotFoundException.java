package com.playtika.sales.exception;

public class CarWasNotFoundException extends RuntimeException {
    public CarWasNotFoundException(long carId) {
        super("Car with id = [" + carId + "] doesn't exist");
    }
}
