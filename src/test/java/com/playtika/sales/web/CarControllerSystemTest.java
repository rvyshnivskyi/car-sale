package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerSystemTest {
    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @TestConfiguration
    public static class TestConfigurationContext {
        @Bean
        public DemoRunner runner() {
            DemoRunner runner = new DemoRunner();
            return runner;
        }

    }

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void addCarReturnCreatedCarId() throws Exception {
        Long maxExistId = context.getBean(CarService.class)
                .getAllCars().stream()
                .mapToLong(Car::getId)
                .max().getAsLong();
        Long result = Long.valueOf(
                mockMvc.perform(post("/cars?price=0.1&firstName=firstName&phone=1234&lastName=lastName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getCarJSON("4")))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn()
                        .getResponse()
                        .getContentAsString());
        assertThat(result, greaterThan(maxExistId));
        context.getBean(CarService.class).deleteSaleDetails(result);
    }

    @Test
    public void getAllCarsReturnArrayOfCars() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("[" + getCarJSON("1") + "," + getCarJSON("2") + "]"));
    }

    @Test
    public void getCarSaleDetailsByCarId() throws Exception {
        mockMvc.perform(get("/cars/1/saleDetails"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"price\":0.1,\"ownerFirstName\":\"firstName\",\"ownerPhoneNumber\":\"1234\",\"ownerLastName\":\"lastName\",\"carId\":1}"));
    }

    @Test
    public void deleteCarById() throws Exception {
        Long maxExistId = context.getBean(CarService.class)
                .getAllCars().stream()
                .mapToLong(Car::getId)
                .max().getAsLong();
        mockMvc.perform(delete("/cars/" + maxExistId))
                .andExpect(status().isOk());
        context.getBean(CarService.class)
                .addCarForSale(generateCar("3"), generateSaleDetails());
    }

    private String getCarJSON(final String number) {
        return "{\"brand\":\"BMW\",\"color\":\"red\",\"age\":2009,\"number\":\"" + number + "\"}";
    }


    public static class DemoRunner implements CommandLineRunner {
        @Autowired
        private CarService carService;

        @Override
        public void run(String... strings) throws Exception {
            carService.addCarForSale(generateCar("1"), generateSaleDetails());
            carService.addCarForSale(generateCar("2"), generateSaleDetails());
        }
    }

    static Car generateCar(String number) {
        Car car = new Car();
        car.setBrand("BMW");
        car.setNumber(number);
        car.setColor("red");
        car.setAge(2009);
        return car;
    }

    static SaleDetails generateSaleDetails() {
        SaleDetails saleDetails = new SaleDetails();
        saleDetails.setPrice(0.1);
        saleDetails.setOwnerFirstName("firstName");
        saleDetails.setOwnerPhoneNumber("1234");
        saleDetails.setOwnerLastName("lastName");
        return saleDetails;
    }
}