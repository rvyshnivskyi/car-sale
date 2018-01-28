package com.playtika.sales.service;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.Person;
import com.playtika.sales.domain.SaleDetails;

import java.util.List;
import java.util.Optional;

public interface CarService {
    Car addCarForSale(Car car, SaleDetails saleDetails);

    List<Car> getAllCars();

    Optional<SaleDetails> getSaleDetails(long id);

    boolean deleteSaleDetails(long id);

    Person getCarOwner(long carId);
}
