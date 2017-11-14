package com.playtika.sales;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import com.playtika.sales.web.CarController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CarControllerRestTest {
    @Mock
    CarService service;

    MockMvc mockMvc;
    Car car = new Car();
    String brand = "BMW";
    String color = "red";
    int age = 10;
    String number = "SA2121SS";
    String carJSONString = "{\"brand\":\"BMW\",\"color\":\"red\",\"age\":10,\"number\":\"SA2121SS\"}";

    SaleDetails saleDetails = new SaleDetails();

    @Before
    public void setUp() throws Exception {
        car.setBrand(brand);
        car.setColor(color);
        car.setAge(age);
        car.setNumber(number);
        saleDetails.setPrice(0.1);
        saleDetails.setOwnerFirstName("firstName");
        saleDetails.setOwnerPhoneNumber("1234");
        saleDetails.setOwnerLastName("lastName");
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CarController(service))
                .build();
    }

    @Test
    public void addCarReturnCreatedCarId() throws Exception {
        Car returnedCar = car;
        long returnedId = 1L;
        returnedCar.setId(returnedId);
        when(service.addCarForSale(car, saleDetails)).thenReturn(returnedCar);
        mockMvc.perform(post("/car?price=0.1&firstName=firstName&phone=1234&lastName=lastName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJSONString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("1"));
    }

    @Test
    public void getAllCarsReturnArrayOfCars() throws Exception {
        when(service.getAllCars()).thenReturn(Arrays.asList(car, car));
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("[" + carJSONString + "," + carJSONString + "]"));
    }

    @Test
    public void getCarSaleDetailsByCarId() throws Exception {
        when(service.getSaleDetailsByCarId(1L)).thenReturn(saleDetails);
        mockMvc.perform(get("/cars/1/saleDetails"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"price\":0.1,\"ownerFirstName\":\"firstName\",\"ownerPhoneNumber\":\"1234\",\"ownerLastName\":\"lastName\",\"carId\":null}"));
    }

    @Test
    public void getCarSaleThrowExceptionWhenNotExist() throws Exception {
        when(service.getSaleDetailsByCarId(0L)).thenThrow(IllegalArgumentException.class);
        mockMvc.perform(get("/cars/0/saleDetails"))
                .andExpect(status().is(404));
    }

    @Test
    public void successDeletingOfCarSale() throws Exception {
        when(service.deleteSaleDetailsByCarId(0L)).thenReturn(true);
        mockMvc.perform(delete("/sale?carId=0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("true"));
    }

    @Test
    public void failedDeletingOfCarSale() throws Exception {
        when(service.deleteSaleDetailsByCarId(0L)).thenReturn(false);
        mockMvc.perform(delete("/sale?carId=0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("false"));
    }
}