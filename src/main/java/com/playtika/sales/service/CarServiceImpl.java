package com.playtika.sales.service;

import com.playtika.sales.dao.CarDao;
import com.playtika.sales.dao.SalePropositionDao;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CarServiceImpl implements CarService {

    private CarDao carDao;
    private SalePropositionDao salePropDao;

    public CarServiceImpl(CarDao carDao, SalePropositionDao salePropDao) {
        this.carDao = carDao;
        this.salePropDao = salePropDao;
    }

    @Override
    public Car addCarForSale(Car car, SaleDetails saleDetails) {
        log.debug("Try to insert new Person into the database");
        PersonEntity owner = PersonEntity.builder()
                .firstName(saleDetails.getOwnerFirstName())
                .lastName(saleDetails.getOwnerLastName())
                .phoneNumber(saleDetails.getOwnerPhoneNumber())
                .build();

        log.debug("Try to insert new Car into the database");
        CarEntity carEntity = CarEntity.builder()
                .brand(car.getBrand())
                .color(car.getColor())
                .owner(owner)
                .year(car.getAge())
                .plateNumber(car.getNumber())
                .build();

        log.debug("Try to insert new SaleProposition into the database");
        SalePropositionEntity propositionEntity = new SalePropositionEntity();
        propositionEntity.setCar(carEntity);
        propositionEntity.setPrice(saleDetails.getPrice());

        return convertToCar(salePropDao.save(propositionEntity).getCar());
    }

    @Override
    public List<Car> getAllCars() {
        return carDao.findAll().stream()
                .map(this::convertToCar)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SaleDetails> getSaleDetails(long id) {
        return salePropDao.findByCar_IdAndStatus(id, SalePropositionEntity.Status.OPEN) .stream().map(sp -> convertToSaleDetails(sp)).findFirst();
    }

    @Override
    public boolean deleteSaleDetails(long id) {
        if (salePropDao.deleteByCar_IdAndStatus(id, SalePropositionEntity.Status.OPEN) < 1){
            log.warn("Sale of car with id = [{}] wasn't found, maybe it was removed before", id);
            return false;
        }
        log.info("Sale of car with id = [{}] was deleted successfully", id);
        return true;
    }

    private Car convertToCar(CarEntity ce) {
        return Car.builder()
                .id(ce.getId())
                .age(ce.getYear())
                .brand(ce.getBrand())
                .number(ce.getPlateNumber())
                .color(ce.getColor()).build();
    }

    private SaleDetails convertToSaleDetails(SalePropositionEntity spe) {
        return SaleDetails.builder()
                .carId(spe.getCar().getId())
                .ownerPhoneNumber(spe.getCar().getOwner().getPhoneNumber())
                .ownerFirstName(spe.getCar().getOwner().getFirstName())
                .ownerLastName(spe.getCar().getOwner().getLastName())
                .price(spe.getPrice()).build();
    }
}