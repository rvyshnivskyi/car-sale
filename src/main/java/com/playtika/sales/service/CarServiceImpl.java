package com.playtika.sales.service;

import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class CarServiceImpl implements CarService {
    private final AtomicLong CURRENT_CAR_ID = new AtomicLong(0);
    private final Map<Long, Car> cars = new ConcurrentHashMap<>();
    private final Map<Long, SaleDetails> sales = new ConcurrentHashMap<>();

    @Override
    public Car addCarForSale(Car car, SaleDetails saleDetails) {
        long id = CURRENT_CAR_ID.incrementAndGet();
        car.setId(id);

        log.debug("Try to add new car into the car list");
		cars.put(id, car);
        log.info("Car {} was added into the car list", car);
        log.debug("Try to add new sale into the sales list");
		sales.put(car.getId(), saleDetails);
        log.info("Sale {} was added into the sales list", car);
        return car;
    }

    @Override
    public List<Car> getAllCars() {
        return new ArrayList<>(cars.values());
    }

    @Override
    public Optional<SaleDetails> getSaleDetails(long id) {
        return ofNullable(sales.get(id));
    }

    @Override
    public boolean deleteSaleDetails(long id) {
        if (sales.remove(id) == null || cars.remove(id) == null) {
            log.warn("Sale of car with id = [{}] wasn't found, maybe it was removed before", id);
            return false;
        }
        log.info("Sale of car with id = [{}] was deleted successfully", id);
        return true;
    }
}