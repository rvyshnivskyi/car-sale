package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
public class CarController {

	private final CarService service;

	public CarController(CarService service) {
		this.service = service;
	}

	@PostMapping(value = "/car")
	public long addCar(@RequestBody @Valid Car car,
					   @RequestParam("price") @Valid double price,
					   @RequestParam("firstName") @Valid String ownerFirstName,
					   @RequestParam("phone") @Valid String ownerPhoneNumber,
					   @RequestParam(value = "lastName", required = false) String ownerLastName) {
		SaleDetails sale = new SaleDetails();
		sale.setPrice(price);
		sale.setOwnerFirstName(ownerFirstName);
		sale.setOwnerPhoneNumber(ownerPhoneNumber);
		sale.setOwnerLastName(ownerLastName);
		Car created = service.addCarForSale(car, sale);
		return created.getId();
	}

	@GetMapping(value = "/cars", produces = APPLICATION_JSON_UTF8_VALUE)
	public List<Car> gerAllCars() {
		return service.getAllCars();
	}

	@GetMapping(value = "/cars/{id}/saleDetails", produces = APPLICATION_JSON_UTF8_VALUE)
	public SaleDetails getCarSaleDetailsById(@PathVariable long id) {
		try {
			return service.getSaleDetailsByCarId(id);
		} catch (IllegalArgumentException ex) {
			throw new CarIdWasNotFoundException(ex.getMessage());
		}
	}

	@DeleteMapping(value = "/sale")
	public boolean deleteSaleDetailsByCarId(@RequestParam("carId") long id) {
		return service.deleteSaleDetailsByCarId(id);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	public class CarIdWasNotFoundException extends IllegalArgumentException {
		public CarIdWasNotFoundException(String message) {
		    super(message);
		    log.error(message, fillInStackTrace());
        }
	}
}