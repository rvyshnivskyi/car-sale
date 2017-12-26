package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@Validated
@Api(value = "carsales", basePath = "cars", description = "Operations to add and delete car sales details",
produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, protocols = "http")
public class CarController {

	private final CarService service;

	public CarController(CarService service) {
		this.service = service;
	}

	@ApiOperation(value = "Add car for selling", response = Long.class)
	@ApiResponses(@ApiResponse(code = 409, message = "Car can't be added because car details duplicate"))
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
		return service.addCarForSale(car, sale).getId();
	}

	@ApiOperation(value = "View a list of available cars")
	@GetMapping(value = "/cars", produces = APPLICATION_JSON_UTF8_VALUE)
	public List<Car> getAllCars() {
		return service.getAllCars();
	}

	@ApiOperation(value = "View sale details of specific car", response = SaleDetails.class)
	@ApiResponses(@ApiResponse(code = 404, message = "Sale of car with specific ID wasn't found"))
	@GetMapping(value = "/cars/{id}/saleDetails", produces = APPLICATION_JSON_UTF8_VALUE)
	public SaleDetails getSaleDetails(@PathVariable long id) {
		return service.getSaleDetails(id)
				.orElseThrow(() -> new SaleDetailsWasNotFoundException(id));
	}

	@ApiOperation(value = "Delete sale details of specific car", response = ResponseEntity.class)
	@ApiResponses(@ApiResponse(code = 204, message = "Car with specific ID was not found"))
	@DeleteMapping(value = "/cars/{id}")
	public ResponseEntity deleteSaleDetails(@PathVariable long id) {
		if(!service.deleteSaleDetails(id)) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return null;
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	public class SaleDetailsWasNotFoundException extends IllegalArgumentException {
		public SaleDetailsWasNotFoundException(long id) {
			super(format("Sale of car with id = [%d] wasn't found", id));
		    log.warn("Sale of car with id = {} wasn't found", id);
        }
	}
}