package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
@Validated
@Api(value = "carsales", basePath = "cars", description = "Operations to add and delete car sales details",
produces = "application/json", consumes = "application/json", protocols = "http")
public class CarController {

	private final CarService service;

	public CarController(CarService service) {
		this.service = service;
	}

	@ApiOperation(value = "Add car for selling", response = Long.class)
	@PostMapping(value = "/cars")
	public long addCar(@RequestBody @Valid Car car,
					   @RequestParam("price") double price,
					   @RequestParam("firstName") String ownerFirstName,
					   @RequestParam("phone") String ownerPhoneNumber,
					   @RequestParam(value = "lastName", required = false) String ownerLastName) {
		SaleDetails sale = SaleDetails.builder()
				.price(price)
				.ownerFirstName(ownerFirstName)
				.ownerPhoneNumber(ownerPhoneNumber)
				.ownerLastName(ownerLastName).build();
		Car created = service.addCarForSale(car, sale);
		return created.getId();
	}

	@ApiOperation(value = "View a list of available cars")
	@GetMapping(value = "/cars", produces = APPLICATION_JSON_UTF8_VALUE)
	public List<Car> getAllCars() {
		return service.getAllCars();
	}

	@ApiOperation(value = "View sale details of specific car", response = SaleDetails.class)
	@GetMapping(value = "/cars/{id}/saleDetails", produces = APPLICATION_JSON_UTF8_VALUE)
	public SaleDetails getSaleDetails(@PathVariable long id) {
		return service.getSaleDetails(id)
				.orElseThrow(() -> new CarIdWasNotFoundException(id));
	}

	@ApiOperation(value = "Delete sale details of specific car", response = ResponseEntity.class)
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