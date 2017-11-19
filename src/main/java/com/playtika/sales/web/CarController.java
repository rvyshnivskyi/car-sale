package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
public class CarController {

	private final CarService service;

	public CarController(CarService service) {
		this.service = service;
	}

	@PostMapping(value = "/cars")
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
	public SaleDetails getSaleDetails(@PathVariable long id) {
		return service.getSaleDetails(id)
				.orElseThrow(() -> new CarIdWasNotFoundException(id));
	}

	@DeleteMapping(value = "/cars/{id}")
	public ResponseEntity deleteSaleDetails(@PathVariable long id) {
		if(!service.deleteSaleDetails(id)) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return null;
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	public class CarIdWasNotFoundException extends IllegalArgumentException {
		public CarIdWasNotFoundException(long id) {
		    super(format("Sale of car with id = [%d] wasn't found", id));
        }
	}
}