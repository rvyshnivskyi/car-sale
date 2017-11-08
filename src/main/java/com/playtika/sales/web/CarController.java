package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
public class CarController {
	private static final AtomicLong CURRENT_CAR_ID = new AtomicLong(0);
	private final List cars = new ArrayList();
	private final Map<Long, SaleDetails> sales = new HashMap<>();

	@PostMapping(value = "/car")
	public long addCar(@RequestBody Car car,
					   @RequestParam("price") double price,
					   @RequestParam("firstName") String ownerFirstName,
					   @RequestParam("phone") String ownerPhoneNumber,
					   @RequestParam(value = "lastName", required = false) String ownerLastName) {
		long id = CURRENT_CAR_ID.addAndGet(1L);
		car.setId(id);
		log.debug("Try to add new car into the car list");
		cars.add(car);
		log.info("Car {} was added into the car list", car);
		log.debug("Try to add new sale into the sales list");
		sales.put(id, new SaleDetails(car.getId(), price, ownerFirstName, ownerPhoneNumber, ownerLastName));
		log.info("Sale {} was added into the sales list", car);
		return car.getId();
	}

	@GetMapping(value = "/cars", produces = APPLICATION_JSON_UTF8_VALUE)
	public List<Car> gerAllCars() {
		return cars;
	}

	@GetMapping(value = "/cars/{id}/saleDetails", produces = APPLICATION_JSON_UTF8_VALUE)
	public SaleDetails getCarSaleDetailsById(@PathVariable long id) {
		return ofNullable(sales.get(id))
				.orElseThrow(() -> new CarIdWasNotFoundException(
						format("Sale of car with [%d] id wasn't found", id)));
	}

	@DeleteMapping(value = "/sale")
	public void deleteCar(@RequestParam("carId") long id) {
		if (sales.remove(id) == null) {
			log.warn("Sale of car with [{}] id wasn't found, maybe it was removed before", id);
		} else {
			log.info("Sale of car [{}] was deleted successfully", id);
		}
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	private class CarIdWasNotFoundException extends IllegalArgumentException {
		public CarIdWasNotFoundException(String message) {
			super(message);
		}
	}
}
