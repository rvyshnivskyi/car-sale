package com.playtika.sales.service;

import com.playtika.sales.dao.CarDao;
import com.playtika.sales.dao.SalePropositionDao;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceImplTest {

    @Mock
    CarDao carDao;

    @Mock
    SalePropositionDao propositionDao;

    CarService carService;

    @Before
    public void setUp() throws Exception {
        carService = new CarServiceImpl(carDao, propositionDao);
    }

    @Test
    public void addCarReturnInsertedCar() {
        Long carId = 1L;
        String carNumber = "aa1";
        when(propositionDao.save(notNull(SalePropositionEntity.class)))
                .thenReturn(generatePropEntity(generateCarEntity(carId, new PersonEntity(), carNumber)));

        Car result = carService.addCarForSale(generateCar(null, "aa1"), generateSaleDetails(carId));
        assertThat(result, is(generateCar(carId, carNumber)));
    }

    @Test
    public void getAllCarsReturnCarList() {
        CarEntity firstCarEntity = generateCarEntity(1L, new PersonEntity(), "11");
        CarEntity secondCarEntity = generateCarEntity(2L, new PersonEntity(), "22");

        when(carDao.findAll()).thenReturn(Arrays.asList(firstCarEntity, secondCarEntity));
        List<Car> result = carService.getAllCars();
        assertThat(result, hasSize(2));
        assertThat(result, hasItems(is(generateCar(1L, "11")), is(generateCar(2L, "22"))));
    }

    @Test
    public void getCarSaleWithNotExistId() {
        long carId = 0L;
        when(propositionDao.findByCarIdAndStatus(carId, SalePropositionEntity.Status.OPEN))
                .thenReturn(Collections.emptyList());
        Optional<SaleDetails> result = carService.getSaleDetails(carId);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void getCarSaleReturnSaleDetails() {
        long carId = 1L;
        CarEntity returnedCarEntity = generateCarEntity(carId, generateOwnerEntity(), "11");

        when(propositionDao.findByCarIdAndStatus(carId, SalePropositionEntity.Status.OPEN))
                .thenReturn(Arrays.asList(generatePropEntity(returnedCarEntity)));
        Optional<SaleDetails> result = carService.getSaleDetails(carId);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(generateSaleDetails(carId)));
    }

    @Test
    public void deleteCarDetailsReturnFalseWithNotExistCarId() {
        long carId = 2L;
        when(propositionDao.deleteByCarIdAndStatus(carId, SalePropositionEntity.Status.OPEN))
                .thenReturn(0);
        boolean result = carService.deleteSaleDetails(carId);
        assertThat(result, is(false));
    }

    @Test
    public void deleteCarDetailsReturnTrueWithExistCarId() {
        long carId = 2L;
        when(propositionDao.deleteByCarIdAndStatus(carId, SalePropositionEntity.Status.OPEN))
                .thenReturn(1);
        boolean result = carService.deleteSaleDetails(carId);
        assertThat(result, is(true));
    }

    private PersonEntity generateOwnerEntity() {
        return PersonEntity.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("1234567")
                .build();
    }

    private SaleDetails generateSaleDetails(Long carId) {
        SaleDetails sale = new SaleDetails();
        sale.setCarId(carId);
        sale.setPrice(123.4);
        sale.setOwnerFirstName("firstName");
        sale.setOwnerPhoneNumber("1234567");
        sale.setOwnerLastName("lastName");
        return sale;
    }

    private CarEntity generateCarEntity(Long id, PersonEntity owner, String number) {
        return CarEntity.builder()
                .id(id)
                .brand("BMW")
                .color("red")
                .owner(owner)
                .plateNumber(number)
                .year(2003)
                .build();
    }

    private SalePropositionEntity generatePropEntity(CarEntity car) {
        SalePropositionEntity propositionEntity = new SalePropositionEntity();
        propositionEntity.setPrice(123.4);
        propositionEntity.setCar(car);
        return propositionEntity;
    }

    private Car generateCar(Long id, String number) {
        Car car = new Car();
        car.setId(id);
        car.setColor("red");
        car.setNumber(number);
        car.setBrand("BMW");
        car.setAge(2003);
        return car;
    }
}
