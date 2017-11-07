package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Controller
public class CarController {
    private List<Car> cars = new ArrayList<>();
    public static final Logger LOG = LoggerFactory.getLogger(CarController.class);

    @PostMapping(value = "/car")
    public @ResponseBody int addCar(@RequestBody Car car,
                                    @RequestParam("price") double price,
                                    @RequestParam("firstName") String ownerFirstName,
                                    @RequestParam("phone") Long ownerPhoneNumber,
                                    @RequestParam(value = "lastName", required = false) String ownerLastName) {
        car.setSaleDetails(new SaleDetails(price, ownerFirstName, ownerLastName, ownerPhoneNumber));
        LOG.debug("Try to add new car into the car list");
        cars.add(car);
        LOG.info("Car {} was added into the car list", car);
        return car.getId();
    }

    @GetMapping(value = "/cars", produces = APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<Car> gerAllCars() {
        return cars;
    }

    @GetMapping(value = "/cars/{id}/saleDetails", produces = APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody SaleDetails getCarSaleDetailsById(@PathVariable int id, HttpServletResponse response) {
        for (Car car : cars) {
            if (car.getId() == id) {
                return car.getSaleDetails();
            }
        }
        throw new CarIdWasNotFoundException("Car with [" + id + "] id wasn't found");
    }

    @DeleteMapping(value = "/car")
    public @ResponseBody void deleteCar(@RequestParam("id") int id) {
        if (!cars.removeIf(car -> car.getId() == id)) {
            throw new CarIdWasNotFoundException("Car with [" + id + "] id wasn't found");
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class CarIdWasNotFoundException extends IllegalArgumentException {
        public CarIdWasNotFoundException(String message) {
            super(message);
        }
    }
}
