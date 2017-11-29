package com.playtika.sales.service;

import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.SaleDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    @PersistenceContext
    protected EntityManager em;

    public CarServiceImpl(EntityManager entityManager) {
        em = entityManager;
    }

    @Override
    @Transactional
    public Car addCarForSale(Car car, SaleDetails saleDetails) {
        log.debug("Try to insert new Person into the database");
        PersonEntity owner = PersonEntity.builder()
                .firstName(saleDetails.getOwnerFirstName())
                .lastName(saleDetails.getOwnerLastName())
                .phoneNumber(saleDetails.getOwnerPhoneNumber())
                .city("Default City")
                .build();
        em.persist(owner);

        log.debug("Try to insert new Car into the database");
        CarEntity carEntity = CarEntity.builder()
                .brand(car.getBrand())
                .color(car.getColor())
                .owner(owner)
                .year(car.getAge())
                .plateNumber(car.getNumber())
                .build();
        em.persist(carEntity);

        log.debug("Try to insert new SakeProposition into the database");
        SalePropositionEntity propositionEntity = SalePropositionEntity.builder()
                .car(carEntity)
                .price(saleDetails.getPrice())
                .build();
        em.persist(propositionEntity);

        car.setId(owner.getId());
        return car;
    }

    @Override
    @Transactional
    public List<Car> getAllCars() {
        Query query = em.createQuery("select c from CarEntity c");
        List<CarEntity> cars = query.getResultList();
        return cars.stream()
                .map(this::convertToCar)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<SaleDetails> getSaleDetails(long id) {
        Query query = em.createQuery(
                "select sp from SalePropositionEntity sp inner join fetch sp.car as c " +
                "where c.id = :carId and sp.status = :status");
        query.setParameter("carId", id);
        query.setParameter("status", SalePropositionEntity.Status.OPEN);

        SalePropositionEntity spe;
        try {
            spe = (SalePropositionEntity) query.getSingleResult();
        } catch (NoResultException ex) {
            return Optional.empty();
        }

        return ofNullable(convertToSaleDetails(spe));
    }

    @Override
    @Transactional
    public boolean deleteSaleDetails(long id) {
        Query query = em.createQuery(
                "select sp from SalePropositionEntity sp inner join fetch sp.car as c " +
                        "where c.id = :carId and sp.status = :status");
        query.setParameter("carId", id);
        query.setParameter("status", SalePropositionEntity.Status.OPEN);

        try {
            SalePropositionEntity spe = (SalePropositionEntity) query.getSingleResult();
            em.remove(spe);
        } catch (NoResultException ex) {
            log.warn("Sale of car with id = [{}] wasn't found, maybe it was removed before", id);
            return false;
        }
        log.info("Sale of car with id = [{}] was deleted successfully", id);
        return true;
    }

    private Car convertToCar(CarEntity ce) {
        Car car = new Car();
        car.setId(ce.getId());
        car.setAge(ce.getYear());
        car.setBrand(ce.getBrand());
        car.setNumber(ce.getPlateNumber());
        car.setColor(ce.getColor());
        return car;
    }

    private SaleDetails convertToSaleDetails(SalePropositionEntity spe) {
        SaleDetails sd = new SaleDetails();
        sd.setCarId(spe.getCar().getId());
        sd.setOwnerPhoneNumber(spe.getCar().getOwner().getPhoneNumber());
        sd.setOwnerFirstName(spe.getCar().getOwner().getFirstName());
        sd.setOwnerLastName(spe.getCar().getOwner().getLastName());
        sd.setPrice(spe.getPrice());
        return sd;
    }
}