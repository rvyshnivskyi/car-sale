package com.playtika.sales.service;

import com.playtika.sales.dao.OfferDao;
import com.playtika.sales.dao.SalePropositionDao;
import com.playtika.sales.dao.entity.OfferEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity.Status;
import com.playtika.sales.domain.Offer;
import com.playtika.sales.domain.Person;
import com.playtika.sales.exception.ActiveOfferWithThisIdWasNotFoundException;
import com.playtika.sales.exception.SaleProposeNotFoundForThisCarException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static com.playtika.sales.dao.entity.OfferEntity.Status.*;
import static com.playtika.sales.dao.entity.SalePropositionEntity.Status.CLOSED;
import static com.playtika.sales.dao.entity.SalePropositionEntity.Status.OPEN;
import static java.util.Comparator.comparing;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferDao offerDao;
    private final SalePropositionDao salePropDao;

    @Override
    public Offer addOfferForSalePropose(Offer offer, long carId) {
        log.debug("Try to insert new Person into the database");
        PersonEntity personEntity = new PersonEntity();
        personEntity.setFirstName(offer.getBuyerFirstName());
        personEntity.setLastName(offer.getBuyerLastName());
        personEntity.setPhoneNumber(offer.getBuyerPhoneNumber());

        log.debug("Try to insert new Offer into the database");

        return convertToOffer(offerDao.save(getOfferEntity(offer, carId, personEntity)));
    }

    @Override
    public List<Offer> getAllActiveOffers(long carId) {
        return offerDao.findAllOffersByCarIdAndStatus(carId, ACTIVE).stream()
                .map(this::convertToOffer)
                .sorted(comparing(Offer::getPrice).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Person acceptActiveOffer(long offerId) {
        OfferEntity offer = offerDao.findFirstByIdAndStatus(offerId, ACTIVE)
                .orElseThrow(() -> new ActiveOfferWithThisIdWasNotFoundException(offerId));
        offer.setStatus(ACCEPTED);
        SalePropositionEntity salePropose = offer.getSale();
        salePropose.setStatus(CLOSED);
        salePropose.getCar().setOwner(offer.getBuyer());
        salePropose.getOffers().stream()
                .filter(o -> o.getStatus() != ACCEPTED)
                .forEach(o -> o.setStatus(DECLINED));
        return convertToPerson(offerDao.save(offer).getSale().getCar().getOwner());
    }

    private OfferEntity getOfferEntity(Offer offer, long carId, PersonEntity personEntity) {
        OfferEntity offerEntity = new OfferEntity();
        offerEntity.setPrice(offer.getPrice());
        offerEntity.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        offerEntity.setBuyer(personEntity);
        offerEntity.setSale(getSaleProposeByCarIdAndStatus(carId, OPEN).stream()
                .findFirst()
                .orElseThrow(() -> new SaleProposeNotFoundForThisCarException(carId)));
        return offerEntity;
    }

    private Offer convertToOffer(OfferEntity offer) {
        return Offer.builder()
                .id(offer.getId())
                .buyerFirstName(offer.getBuyer().getFirstName())
                .buyerLastName(offer.getBuyer().getLastName())
                .buyerPhoneNumber(offer.getBuyer().getPhoneNumber())
                .price(offer.getPrice())
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

    private List<SalePropositionEntity> getSaleProposeByCarIdAndStatus(long id, Status status) {
        return salePropDao.findByCarIdAndStatus(id, status);
    }
}
