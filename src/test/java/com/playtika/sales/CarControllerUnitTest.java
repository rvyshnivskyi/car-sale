package com.playtika.sales;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import com.playtika.sales.web.CarController;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarControllerUnitTest {
    @Mock
    CarService service;

    CarController controller;

    Car car = new Car();
    SaleDetails saleDetails = new SaleDetails();

    @Before
    public void setUp() throws Exception {
        controller = new CarController(service);
        saleDetails.setPrice(0.1);
        saleDetails.setOwnerFirstName("firstName");
        saleDetails.setOwnerPhoneNumber("1234");
        saleDetails.setOwnerLastName("lastName");
    }

    @Test
    public void addCarReturnCarId() {
        Car returnedCar = car;
        long returnedId = 1L;
        returnedCar.setId(returnedId);
        when(service.addCarForSale(car, saleDetails)).thenReturn(returnedCar);
        long result = controller.addCar(car, saleDetails.getPrice(), saleDetails.getOwnerFirstName(), saleDetails.getOwnerPhoneNumber(), saleDetails.getOwnerLastName());
        assertThat(result, is(returnedId));
    }

    @Test
    public void getAllCarsWorksCorrectly() {
        Car second = new Car();
        when(service.getAllCars()).thenReturn(Arrays.asList(car, second));
        List<Car> result = controller.gerAllCars();
        assertThat(result, hasSize(2));
        assertThat(result, hasItems(car, second));
    }

    @Test
    public void getAllCarsWithEmptyCarList() {
        when(service.getAllCars()).thenReturn(emptyList());
        List<Car> result = controller.gerAllCars();
        Assert.assertThat(result, Matchers.hasSize(0));
    }

    @Test
    public void getCarSaleDetailsById() {
        when(service.getSaleDetailsByCarId(1)).thenReturn(saleDetails);
        SaleDetails result = controller.getCarSaleDetailsById(1);
        assertThat(result, is(saleDetails));
    }

    @Test(expected = CarController.CarIdWasNotFoundException.class)
    public void getCarSaleDetailsWithNotExistId() {
        when(service.getSaleDetailsByCarId(0)).thenThrow(IllegalArgumentException.class);
        controller.getCarSaleDetailsById(0);
    }

    @Test
    public void successDeletingOfCarSale() {
        when(service.deleteSaleDetailsByCarId(0)).thenReturn(true);
        boolean result = controller.deleteSaleDetailsByCarId(0);
        Assert.assertTrue(result);
    }

    @Test
    public void failedDeletingOfCarSale() {
        when(service.deleteSaleDetailsByCarId(0)).thenReturn(false);
        boolean result = controller.deleteSaleDetailsByCarId(0);
        Assert.assertFalse(result);
    }
}
