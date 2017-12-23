package com.playtika.sales.service;

import com.playtika.sales.dao.CarDao;
import com.playtika.sales.dao.SalePropositionDao;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    final CarDao carDao;
    final SalePropositionDao salePropDao;

    @Override
    public Car addCarForSale(Car car, SaleDetails saleDetails) {
        log.debug("Try to insert new Person into the database");
        PersonEntity owner = new PersonEntity();
                owner.setFirstName(saleDetails.getOwnerFirstName());
                owner.setLastName(saleDetails.getOwnerLastName());
                owner.setPhoneNumber(saleDetails.getOwnerPhoneNumber());

        log.debug("Try to insert new Car into the database");
        CarEntity carEntity = new CarEntity();
                carEntity.setBrand(car.getBrand());
                carEntity.setColor(car.getColor());
                carEntity.setOwner(owner);
                carEntity.setYear(car.getAge());
                carEntity.setPlateNumber(car.getNumber());

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
        return salePropDao.findByCarIdAndStatus(id, SalePropositionEntity.Status.OPEN) .stream().map(this::convertToSaleDetails).findFirst();
    }

    @Override
    public boolean deleteSaleDetails(long id) {
        if (salePropDao.deleteByCarIdAndStatus(id, SalePropositionEntity.Status.OPEN) < 1){
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