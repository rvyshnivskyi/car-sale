package com.playtika.sales.service;

import com.playtika.sales.dao.CarDao;
import com.playtika.sales.dao.OfferDao;
import com.playtika.sales.dao.SalePropositionDao;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.OfferEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import com.playtika.sales.domain.Car;
import com.playtika.sales.domain.Offer;
import com.playtika.sales.domain.Person;
import com.playtika.sales.domain.SaleDetails;
import com.playtika.sales.exception.ActiveOfferWithThisIdWasNotFoundException;
import com.playtika.sales.exception.CarWasNotFoundException;
import com.playtika.sales.exception.DuplicateCarSaleDetailsException;
import com.playtika.sales.exception.SaleProposeNotFoundForThisCarException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    final CarDao carDao;
    final SalePropositionDao salePropDao;
    private OfferDao offerDao;

    @Override
    public Car addCarForSale(Car car, SaleDetails saleDetails) {
        log.debug("Try to insert new Person into the database");
        PersonEntity owner = generatePersonEntity(
                saleDetails.getOwnerFirstName(),
                saleDetails.getOwnerLastName(),
                saleDetails.getOwnerPhoneNumber());

        log.debug("Try to insert new Car into the database");
        CarEntity carEntity = new CarEntity();
                carEntity.setBrand(car.getBrand());
                carEntity.setColor(car.getColor());
                carEntity.setOwner(owner);
                carEntity.setYear(car.getYear());
                carEntity.setPlateNumber(car.getNumber());

        log.debug("Try to insert new SaleProposition into the database");
        SalePropositionEntity propositionEntity = new SalePropositionEntity();
        propositionEntity.setCar(carEntity);
        propositionEntity.setPrice(saleDetails.getPrice());

        CarEntity result;
        try {
            result = salePropDao.save(propositionEntity).getCar();
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateCarSaleDetailsException(car, ex);
        }
        return convertToCar(result);
    }

    @Override
    public List<Car> getAllCars() {
        return carDao.findAll().stream()
                .map(this::convertToCar)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SaleDetails> getSaleDetails(long id) {
        return getSaleProposeByCarIdAndStatus(id).stream().map(this::convertToSaleDetails).findFirst();
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

    @Override
    public Offer addOfferForSalePropose(Offer offer, long carId) {
        log.debug("Try to insert new Person into the database");
        PersonEntity personEntity = generatePersonEntity(
                offer.getBuyerFirstName(),
                offer.getBuyerLastName(),
                offer.getBuyerPhoneNumber()
        );

        log.debug("Try to insert new Offer into the database");
        OfferEntity offerEntity = new OfferEntity();
        offerEntity.setPrice(offer.getPrice());
        offerEntity.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        offerEntity.setBuyer(personEntity);
        offerEntity.setSale(getSaleProposeByCarIdAndStatus(carId).stream()
                .findFirst()
                .orElseThrow(() -> new SaleProposeNotFoundForThisCarException(carId)));

        return convertToOffer(offerDao.save(offerEntity));
    }

    @Override
    public List<Offer> getAllActiveOffers(long carId) {
        return offerDao.findAllOffersByCarIdAndStatus(carId, OfferEntity.Status.ACTIVE).stream()
                .map(this::convertToOffer)
                .sorted(comparing(Offer::getPrice).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Person acceptActiveOffer(long offerId) {
        OfferEntity offer = offerDao.findFirstByIdAndStatus(offerId, OfferEntity.Status.ACTIVE)
                .orElseThrow(() -> new ActiveOfferWithThisIdWasNotFoundException(offerId));
        offer.setStatus(OfferEntity.Status.ACCEPTED);
        offer.getSale().setStatus(SalePropositionEntity.Status.CLOSED);
        offer.getSale().getCar().setOwner(offer.getBuyer());
        offer.getSale().getOffers().stream()
                .filter(o -> o.getStatus() != OfferEntity.Status.ACCEPTED)
                .forEach(o -> o.setStatus(OfferEntity.Status.DECLINED));
        return convertToPerson(offerDao.save(offer).getSale().getCar().getOwner());
    }

    @Override
    public Person getCarOwner(long carId) {
        return convertToPerson(Optional.ofNullable(carDao.findOne(carId))
                .orElseThrow(() -> new CarWasNotFoundException(carId))
                .getOwner());
    }

    private List<SalePropositionEntity> getSaleProposeByCarIdAndStatus(long id) {
        return salePropDao.findByCarIdAndStatus(id, SalePropositionEntity.Status.OPEN);
    }

    private PersonEntity generatePersonEntity(@NotBlank String ownerFirstName, String ownerLastName, @NotBlank String ownerPhoneNumber) {
        PersonEntity pe = new PersonEntity();
        pe.setFirstName(ownerFirstName);
        pe.setLastName(ownerLastName);
        pe.setPhoneNumber(ownerPhoneNumber);
        return pe;
    }

    private Offer convertToOffer(OfferEntity offer) {
        return Offer.builder()
                .id(offer.getId())
                .buyerFirstName(offer.getBuyer().getFirstName())
                .buyerLastName(offer.getBuyer().getLastName())
                .buyerPhoneNumber(offer.getBuyer().getPhoneNumber())
                .price(offer.getPrice())
                .saleProposeId(offer.getSale().getId())
                .build();
    }

    private Person convertToPerson(PersonEntity personEntity) {
        return Person.builder()
                .id(personEntity.getId())
                .phoneNumber(personEntity.getPhoneNumber())
                .firstName(personEntity.getFirstName())
                .lastName(personEntity.getLastName())
                .build();
    }

    private Car convertToCar(CarEntity ce) {
        return Car.builder()
                .id(ce.getId())
                .year(ce.getYear())
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
