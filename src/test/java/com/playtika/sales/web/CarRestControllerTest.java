package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.Offer;
import com.playtika.sales.domain.Person;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.exception.ActiveOfferWithThisIdWasNotFoundException;
import com.playtika.sales.exception.CarWasNotFoundException;
import com.playtika.sales.exception.DuplicateCarSaleDetailsException;
import com.playtika.sales.exception.SaleProposeNotFoundForThisCarException;
import com.playtika.sales.service.CarService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Collections;
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

@ActiveProfiles("test")
@WebMvcTest(CarController.class)
@RunWith(SpringRunner.class)
public class CarRestControllerTest {
    @MockBean
    CarService service;

    @Autowired
    MockMvc mockMvc;

    public CarRestControllerTest() throws JSONException {
    }

    @Test
    public void addCarReturnsCarId() throws Exception {
        String number = "1";
        Car returned = generateCarWithId(number, 1);
        when(service.addCarForSale(generateCar(number), generateSaleDetails()))
                .thenReturn(returned);
        mockMvc.perform(post("/cars?")
                .param("price", "0.1")
                .param("firstName", "firstName")
                .param("phone", "1234")
                .param("lastName", "lastName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCarJSON(number)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json("1"));
    }

    @Test
    public void addCarReturnsConflictWhenDuplicatePlateNumberAndThrowsException() throws Exception {
        String number = "1";
        Car returned = generateCarWithId(number, 1);
        when(service.addCarForSale(generateCar(number), generateSaleDetails()))
                .thenReturn(returned).thenThrow(new DuplicateCarSaleDetailsException(generateCar(number), new Exception()));
        mockMvc.perform(post("/cars?")
                .param("price", "0.1")
                .param("firstName", "firstName")
                .param("phone", "1234")
                .param("lastName", "lastName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCarJSON(number)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json("1"));
        mockMvc.perform(post("/cars?")
                .param("price", "0.1")
                .param("firstName", "firstName")
                .param("phone", "1234")
                .param("lastName", "lastName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCarJSON(number)))
                .andExpect(status().isConflict());
    }

    @Test
    public void addOfferForSaleProposeReturnOfferId() throws Exception {
        String buyerName = "Roma";
        when(service.addOfferForSalePropose(generateOffer(buyerName), 1L))
                .thenReturn(generateOfferWithId(buyerName, 1L));
        mockMvc.perform(post("/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getOfferJSON(buyerName)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json("1"));
    }

    @Test
    public void addOfferForSaleProposeReturnNotFoundStatusWhenNoSaleProposes() throws Exception {
        String buyerName = "Roma";
        when(service.addOfferForSalePropose(generateOffer(buyerName), 1L))
                .thenThrow(new SaleProposeNotFoundForThisCarException(1L));
        mockMvc.perform(post("/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getOfferJSON(buyerName)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllActiveOffersReturnArraysOffers() throws Exception {
        when(service.getAllActiveOffers(1L))
                .thenReturn(Arrays.asList(generateOffer("Vasia"), generateOffer("Petia")));
        mockMvc.perform(get("/cars/1/offers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json("[" + getOfferJSON("Petia") + "," + getOfferJSON("Vasia") + "]"));
    }

    @Test
    public void getAllActiveOffersReturnEmptyListWhenNoOneOfferForCarExist() throws Exception {
        long carId = 2L;
        when(service.getAllActiveOffers(carId))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/cars/" + carId + "/offers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json("[]"));
    }

    @Test
    public void acceptActiveOfferReturnNewCarOwnerId() throws Exception {
        long offerId = 1;
        Long personId = 2L;
        when(service.acceptActiveOffer(offerId))
                .thenReturn(generatePerson(personId));
        mockMvc.perform(put("/offers/" + offerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json(personId.toString()));
    }

    @Test
    public void acceptActiveOfferReturnNotFoundWhenNoActiveOfferWithCurrentId() throws Exception {
        long offerId = 1;
        when(service.acceptActiveOffer(offerId))
                .thenThrow(ActiveOfferWithThisIdWasNotFoundException.class);
        mockMvc.perform(put("/offers/" + offerId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void carOwnerSuccessfullyReturned() throws Exception {
        long carId = 1L;
        when(service.getCarOwner(carId))
                .thenReturn(generatePerson(1L));
        mockMvc.perform(get("/cars/" + carId + "/owner"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"firstName\":\"Roma\",\"phoneNumber\":\"1234567\",\"lastName\":\"Vysh\"}"));
    }

    @Test
    public void getCarOwnerReturnedNotFoundWhenCarWithThisIdNotExist() throws Exception {
        long carId = 1L;
        when(service.getCarOwner(carId))
                .thenThrow(CarWasNotFoundException.class);
        mockMvc.perform(get("/cars/" + carId + "/owner"))
                .andExpect(status().isNotFound());
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
        assertThat(resolved.getClass(), typeCompatibleWith(CarController.SaleDetailsWasNotFoundException.class));
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

    @Test
    public void carBrandNotBlankValidation() throws Exception {
        MvcResult result = mockMvc.perform(post("/cars?")
                .param("price", "0.1")
                .param("firstName", "firstName")
                .param("phone", "1234")
                .param("lastName", "lastName")
                .contentType(APPLICATION_JSON_UTF8)
                .content(carJSON.put("brand", "").toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(MethodArgumentNotValidException.class));
    }

    @Test
    public void carColorNotBlankValidation() throws Exception {
        MvcResult result = mockMvc.perform(post("/cars?")
                .param("price", "0.1")
                .param("firstName", "firstNAme")
                .param("phone", "1234")
                .param("lastName", "lastName")
                .contentType(APPLICATION_JSON_UTF8)
                .content(carJSON.put("color", "").toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(MethodArgumentNotValidException.class));
    }

    @Test
    public void carNumberNotBlankValidation() throws Exception {
        MvcResult result = mockMvc.perform(post("/cars?")
                .param("price", "0.1")
                .param("firstName", "firstNAme")
                .param("phone", "1234")
                .param("lastName", "lastName")
                .contentType(APPLICATION_JSON_UTF8)
                .content(carJSON.put("number", "").toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(MethodArgumentNotValidException.class));
    }

    @Test
    public void carAgeNotNegativeValidation() throws Exception {
        MvcResult result = mockMvc.perform(post("/cars?")
                .param("price", "0.1")
                .param("firstName", "firstNAme")
                .param("phone", "1234")
                .param("lastName", "lastName")
                .contentType(APPLICATION_JSON_UTF8)
                .content(carJSON.put("year", "-1").toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(MethodArgumentNotValidException.class));
    }

    private Person generatePerson(Long personId) {
        return Person.builder()
                .firstName("Roma")
                .lastName("Vysh")
                .phoneNumber("1234567")
                .id(personId)
                .build();
    }

    private Car generateCar(String number) {
        return Car.builder()
                .brand("BMW")
                .number(number)
                .color("red")
                .year(3).build();
    }

    private Car generateCarWithId(String number, long id) {
        return Car.builder()
                .id(id)
                .brand("BMW")
                .number(number)
                .color("red")
                .year(3).build();
    }

    private SaleDetails generateSaleDetails() {
        return SaleDetails.builder()
                .price(0.1d)
                .ownerFirstName("firstName")
                .ownerPhoneNumber("1234")
                .ownerLastName("lastName").build();
    }

    private Offer generateOfferWithId(String buyerName, long id) {
        return Offer.builder()
                .buyerFirstName(buyerName)
                .buyerLastName("Vyshnivskyi")
                .buyerPhoneNumber("380960000000")
                .price(2000.2)
                .id(id)
                .build();
    }

    private Offer generateOffer(String buyerName) {
        return Offer.builder()
                .buyerFirstName(buyerName)
                .buyerLastName("Vyshnivskyi")
                .buyerPhoneNumber("380960000000")
                .price(2000.2)
                .build();
    }

    private String getOfferJSON(String buyerName) {
        return "{\"price\":2000.2,\"buyerFirstName\":\"" + buyerName + "\",\"buyerPhoneNumber\":\"380960000000\",\"buyerLastName\":\"Vyshnivskyi\"}";
    }

    private String getCarJSON(final String number) throws JSONException {
        return carJSON.put("number", number).toString();
    }

    JSONObject carJSON = new JSONObject("{\"brand\":\"BMW\",\"color\":\"red\",\"year\":3,\"number\":\"number\"}");
}
