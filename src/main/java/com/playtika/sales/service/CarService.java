package com.playtika.sales.service;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;

import java.util.List;

public interface CarService {
    Car addCarForSale(Car car, SaleDetails saleDetails);

    List<Car> getAllCars();

    SaleDetails getSaleDetailsByCarId(long id);

    boolean deleteSaleDetailsByCarId(long id);
}
