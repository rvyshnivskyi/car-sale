package com.playtika.sales.service;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CarServiceImplTest {
    CarService carService = new CarServiceImpl();
    private Car firstCar = new Car();
    private Car secondCar = new Car();

    @Before
    public void setUp() throws Exception {
        carService.addCarForSale(firstCar, generateSaleDetails());
        carService.addCarForSale(secondCar, generateSaleDetails());
    }

    @Test
    public void addCarGeneratePositiveId() {
        Car result = carService.addCarForSale(new Car(), generateSaleDetails());
        assertThat(result.getId(), greaterThan(0L));
    }

    @Test
    public void addCarGenerateDifferentIds() {
        Car firstResult = carService.addCarForSale(new Car(), generateSaleDetails());
        Car secondResult = carService.addCarForSale(new Car(), generateSaleDetails());
        assertThat(firstResult.getId(), not(secondResult.getId()));
    }

    @Test
    public void getAllCarsReturnCarList() {
        List<Car> result = carService.getAllCars();
        assertThat(result, hasSize(2));
        assertThat(result, hasItems(is(firstCar), is(secondCar)));
    }

    @Test
    public void getCarSaleWithNotExistId() {
        Optional<SaleDetails> result = carService.getSaleDetails(0L);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void getCarSaleReturnSaleDetails() {
        Optional<SaleDetails> result = carService.getSaleDetails(1L);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(generateSaleDetails()));
    }

    @Test
    public void deleteCarDetailsReturnFalseWithNotExistCarId() {
        boolean result = carService.deleteSaleDetails(0);
        assertThat(result, is(false));
    }

    @Test
    public void deleteCarDetailsReturnTrueWithNotExistCarId() {
        boolean result = carService.deleteSaleDetails(1);
        assertThat(result, is(true));
    }

    private SaleDetails generateSaleDetails() {
        SaleDetails sale = new SaleDetails();
        sale.setPrice(123.4);
        sale.setOwnerFirstName("firstName");
        sale.setOwnerPhoneNumber("1234567");
        sale.setOwnerLastName("lastName");
        return sale;
    }
}
