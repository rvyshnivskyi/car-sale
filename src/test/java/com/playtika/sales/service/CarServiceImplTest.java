package com.playtika.sales.service;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CarServiceImplTest {

    @Autowired
    EntityManager em;

    CarService carService;

    Car firstCar = new Car();
    Car secondCar = new Car();
    long firstCarId = 0;

    @Before
    public void setUp() throws Exception {
        carService = new CarServiceImpl(em);
        firstCarId = carService.addCarForSale(firstCar, generateSaleDetails()).getId();
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
        Optional<SaleDetails> result = carService.getSaleDetails(firstCarId);
        assertThat(result.isPresent(), is(true));
        SaleDetails expected = generateSaleDetails();
        expected.setCarId(firstCarId);
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    public void deleteCarDetailsReturnFalseWithNotExistCarId() {
        boolean result = carService.deleteSaleDetails(0L);
        assertThat(result, is(false));
    }

    @Test
    public void deleteCarDetailsReturnTrueWithNotExistCarId() {
        boolean result = carService.deleteSaleDetails(firstCarId);
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
