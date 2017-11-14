package com.playtika.sales;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import com.playtika.sales.service.CarServiceImpl;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class CarServiceImplTest {
    CarService carService;

    @Before
    public void setUp() throws Exception {
        carService = new CarServiceImpl();
    }

    @Test
    public void addCarGeneratePositiveId() {
        Car result = carService.addCarForSale(new Car(), generateSaleDetails(123.4, "firstName", "1234567", "lastName"));
        assertThat(result.getId(), Matchers.greaterThan(0L));
    }

    @Test
    public void addCarGenerateDifferentIds() {
        Car firstResult = carService.addCarForSale(new Car(), generateSaleDetails(123.4, "firstName", "1234567", "lastName"));
        Car secondResult = carService.addCarForSale(new Car(), generateSaleDetails(123.4, "firstName", "1234567", "lastName"));
        assertThat(firstResult.getId(), Matchers.not(secondResult.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCarSalesThrowExceptionWithNotExistId() {
        carService.getSaleDetailsByCarId(0L);
    }

    @Test
    public void notExceptionDeleteCarDetailsWithNotExistCarId() {
        carService.deleteSaleDetailsByCarId(0);
    }

    private SaleDetails generateSaleDetails(double price, String ownerFirstName, String ownerPhoneNumber, String ownerLastName) {
        SaleDetails sale = new SaleDetails();
        sale.setPrice(price);
        sale.setOwnerFirstName(ownerFirstName);
        sale.setOwnerPhoneNumber(ownerPhoneNumber);
        sale.setOwnerLastName(ownerLastName);
        return sale;
    }
}
