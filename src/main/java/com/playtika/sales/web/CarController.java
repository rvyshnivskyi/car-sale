package com.playtika.sales.web;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.service.CarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLIntegrityConstraintViolationException;
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
	@ApiResponses(@ApiResponse(code = 409, message = "Car can't be added because of plateNumber duplicate"))
	@PostMapping(value = "/cars")
	public long addCar(@RequestBody @Valid Car car,
					   @RequestParam("price") double price,
					   @RequestParam("firstName") String ownerFirstName,
					   @RequestParam("phone") String ownerPhoneNumber,
					   @RequestParam(value = "lastName", required = false) String ownerLastName) throws DuplicatePlateNumberException {
		SaleDetails sale = SaleDetails.builder()
				.price(price)
				.ownerFirstName(ownerFirstName)
				.ownerPhoneNumber(ownerPhoneNumber)
				.ownerLastName(ownerLastName).build();
		try {
			return service.addCarForSale(car, sale).getId();
		} catch (DataIntegrityViolationException ex) {
			throw new DuplicatePlateNumberException(car.getNumber());
		}
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
	@ApiResponses(@ApiResponse(code = 404, message = "Car with specific ID was not found"))
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

	@ResponseStatus(HttpStatus.CONFLICT)
	public class DuplicatePlateNumberException extends SQLIntegrityConstraintViolationException {
		public DuplicatePlateNumberException(String plateNumber) {
			super(format("Car with plateNumber=[%s] can't be added, cause car with the same plateNumber is already exist", plateNumber));
		}
	}
}