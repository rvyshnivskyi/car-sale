package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CarRestControllerTest {
    @Mock
    CarService service;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CarController(service))
                .build();
    }

    @Test
    public void addCarReturnsCarId() throws Exception {
        String number = "1";
        Car returned = generateCar(number);
        returned.setId(1L);
        when(service.addCarForSale(generateCar(number), generateSaleDetails()))
                .thenReturn(returned);
        mockMvc.perform(post("/cars?price=0.1&firstName=firstName&phone=1234&lastName=lastName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCarJSON(number)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json("1"));
    }

    @Test
    public void getAllCarsReturnsCarsJSONArray() throws Exception {
        when(service.getAllCars()).thenReturn(Arrays.asList(generateCar("1"), generateCar("2")));
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json("[" + getCarJSON("1") + "," + getCarJSON("2") + "]"));
    }

    @Test
    public void getAllCarsWithEmptyCarList() throws Exception {
        when(service.getAllCars()).thenReturn(emptyList());
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json("[]"));
    }

    @Test
    public void getCarSaleDetailsById() throws Exception {
        when(service.getSaleDetails(1)).thenReturn(Optional.of(generateSaleDetails()));
        mockMvc.perform(get("/cars/1/saleDetails"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json("{\"price\":0.1,\"ownerFirstName\":\"firstName\",\"ownerPhoneNumber\":\"1234\",\"ownerLastName\":\"lastName\",\"carId\":null}"));
    }

    @Test
    public void getCarSaleDetailsWithNotExistIdThrowsException() throws Exception {
        when(service.getSaleDetails(0)).thenReturn(empty());
        Exception resolved = mockMvc.perform(get("/cars/0/saleDetails"))
                .andExpect(status().isNotFound()).andReturn().getResolvedException();
        assertThat(resolved.getClass(), typeCompatibleWith(CarController.CarIdWasNotFoundException.class));
    }

    @Test
    public void deleteCarReturnOkStatus() throws Exception {
        when(service.deleteSaleDetails(1)).thenReturn(true);
        mockMvc.perform(delete("/cars/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCarWithNotExistCarReturnNoContentStatus() throws Exception {
        when(service.deleteSaleDetails(0)).thenReturn(false);
        mockMvc.perform(delete("/cars/0"))
                .andExpect(status().isNoContent());
    }

    private Car generateCar(String number) {
        Car car = new Car();
        car.setBrand("BMW");
        car.setNumber(number);
        car.setColor("red");
        car.setAge(3);
        return car;
    }

    private SaleDetails generateSaleDetails() {
        SaleDetails saleDetails = new SaleDetails();
        saleDetails.setPrice(0.1);
        saleDetails.setOwnerFirstName("firstName");
        saleDetails.setOwnerPhoneNumber("1234");
        saleDetails.setOwnerLastName("lastName");
        return saleDetails;
    }

    private String getCarJSON(final String number) {
        return "{\"brand\":\"BMW\",\"color\":\"red\",\"age\":3,\"number\":\"" + number + "\"}";
    }
}
