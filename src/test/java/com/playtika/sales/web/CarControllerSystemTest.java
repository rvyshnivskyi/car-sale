package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.Offer;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import com.playtika.sales.service.OfferService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerSystemTest {
    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CarService carService;

    @Autowired
    OfferService offerService;

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
        long maxCarId = getMaxCarId();
        long result = Long.valueOf(
                mockMvc.perform(post("/cars?price=0.1&firstName=firstName&phone=1234&lastName=lastName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getCarJSON("4")))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn()
                        .getResponse()
                        .getContentAsString());
        assertThat(result, greaterThan(maxCarId));
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
    public void getAllActiveOffersReturnArrayOfOffers() throws Exception {
        mockMvc.perform(get("/cars/" + getMaxCarId() + "/offers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("[" + getOfferJSON("Vova") + "," + getOfferJSON("Tolia") + "]"));
    }

    @Rollback
    @Test
    public void offerAccepterReturnNewOwnerId() throws Exception {
        Long maxOfferId = offerService.getAllActiveOffers(getMinCarId()).stream()
                .mapToLong(Offer::getId)
                .max().getAsLong();
        Long result = Long.valueOf(
                mockMvc.perform(put("/offers/" + maxOfferId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn()
                        .getResponse()
                        .getContentAsString());
        assertThat(result, greaterThan(3L));
    }

    @Test
    public void carOwnerSuccessfullyReturns() throws Exception {
       mockMvc.perform(get("/cars/" + getMaxCarId() + "/owner"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(content().json("{\"firstName\":\"firstName\",\"phoneNumber\":\"1234\",\"lastName\":\"lastName\"}"));
    }

    @Test
    public void getCarSaleDetailsByCarId() throws Exception {
        mockMvc.perform(get("/cars/" + getMaxCarId() + "/saleDetails"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"price\":0.1,\"ownerFirstName\":\"firstName\",\"ownerPhoneNumber\":\"1234\",\"ownerLastName\":\"lastName\"}"));
    }

    @Test
    public void addOfferForSaleProposeReturnOfferId() throws Exception {
        long result = Long.valueOf(
                mockMvc.perform(post("/cars/" + getMaxCarId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getOfferJSON("Roma")))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        );
        assertThat(result, greaterThan(0L));
    }

    @Test
    public void deleteCarById() throws Exception {
        mockMvc.perform(delete("/cars/" + getMaxCarId()))
                .andExpect(status().isOk());
        context.getBean(CarService.class)
                .addCarForSale(generateCar("3"), generateSaleDetails());
    }

    private String getOfferJSON(String buyerName) {
        return "{\"price\":2001.2,\"buyerFirstName\":\"" + buyerName + "\",\"buyerPhoneNumber\":\"380960000000\",\"buyerLastName\":\"Vyshnivskyi\"}";
    }

    private String getCarJSON(final String number) {
        return "{\"brand\":\"BMW\",\"color\":\"red\",\"year\":2009,\"number\":\"" + number + "\"}";
    }


    public static class DemoRunner implements CommandLineRunner {
        @Autowired
        private CarService carService;

        @Autowired
        private OfferService offerService;

        @Override
        public void run(String... strings) throws Exception {
            Long minCarId = carService.addCarForSale(generateCar("1"), generateSaleDetails()).getId();
            Long maxCarId = carService.addCarForSale(generateCar("2"), generateSaleDetails()).getId();
            offerService.addOfferForSalePropose(generateOffer("Vova"), minCarId);
            offerService.addOfferForSalePropose(generateOffer("Tolia"), minCarId);
            offerService.addOfferForSalePropose(generateOffer("Vova"), maxCarId);
            offerService.addOfferForSalePropose(generateOffer("Tolia"), maxCarId);
        }
    }

    static Offer generateOffer(String name) {
        return Offer.builder()
                .price(2001.2)
                .buyerLastName("Vyshnivskyi")
                .buyerPhoneNumber("380960000000")
                .buyerFirstName(name)
                .build();
    }

    long getMaxCarId() {
        return carService.getAllCars().stream()
                .mapToLong(Car::getId)
                .max().getAsLong();
    }

    private long getMinCarId() {
        return carService.getAllCars().stream()
                .mapToLong(Car::getId)
                .min().getAsLong();
    }

    static Car generateCar(String number) {
        return Car.builder()
                .brand("BMW")
                .number(number)
                .color("red")
                .year(2009).build();
    }

    static SaleDetails generateSaleDetails() {
        return SaleDetails.builder()
                .price(0.1)
                .ownerFirstName("firstName")
                .ownerPhoneNumber("1234")
                .ownerLastName("lastName").build();
    }
}
