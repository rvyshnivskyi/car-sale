package com.playtika.sales.service;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.playtika.sales.dao.OfferDao;
import com.playtika.sales.dao.SalePropositionDao;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.OfferEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import com.playtika.sales.domain.Offer;
import com.playtika.sales.domain.Person;
import com.playtika.sales.exception.ActiveOfferWithThisIdWasNotFoundException;
import com.playtika.sales.exception.SaleProposeNotFoundForThisCarException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Date;
import java.util.*;

import static com.playtika.sales.dao.entity.OfferEntity.Status.*;
import static com.playtika.sales.dao.entity.SalePropositionEntity.Status.CLOSED;
import static com.playtika.sales.dao.entity.SalePropositionEntity.Status.OPEN;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OfferServiceImplTest {

    @Mock
    OfferDao offerDao;

    @Mock
    SalePropositionDao propositionDao;

    OfferService offerService;

    @Before
    public void setUp() throws Exception {
        offerService = new OfferServiceImpl(offerDao, propositionDao);
    }

    @Test
    public void addOfferReturnInsertedOffer() throws Exception {
        when(propositionDao.findByCarIdAndStatus(1L, OPEN))
                .thenReturn(Arrays.asList(generateSalePropositionEntity(2L)));
        when(offerDao.save(notNull(OfferEntity.class)))
                .thenReturn(generateOfferEntity(1L, generatePersonEntity(), generateSalePropositionEntity(2L), 222.1));
        Offer result = offerService.addOfferForSalePropose(generateOffer(null, null, 222.1), 1L);
        assertThat(result, is(generateOffer(1L, 2L, 222.1)));
    }

    @Test(expected = SaleProposeNotFoundForThisCarException.class)
    public void addOfferThrowsExceptionWhenNoSalePropsForThisCar() throws Exception {
        when(propositionDao.findByCarIdAndStatus(1L, OPEN))
                .thenReturn(emptyList());
        ;
        offerService.addOfferForSalePropose(generateOffer(null, null, 222.1), 1L);
    }

    @Test
    public void getAllActiveOffersReturnOffersList() throws Exception {
        when(offerDao.findAllOffersByCarIdAndStatus(1L, ACTIVE))
                .thenReturn(Arrays.asList(
                        generateOfferEntity(1L, generatePersonEntity(), generateSalePropositionEntity(2L), 12.3),
                        generateOfferEntity(2L, generatePersonEntity(), generateSalePropositionEntity(2L), 123.4)));
        List<Offer> result = offerService.getAllActiveOffers(1L);
        assertThat(result, hasSize(2));
        assertThat(result, hasItems(generateOffer(1L, 2L, 12.3), generateOffer(2L, 2L, 123.4)));
    }

    @Test
    public void getAllActiveOffersReturnEmptyListIfNoOneOffersForCarExist() throws Exception {
        when(offerDao.findAllOffersByCarIdAndStatus(1L, ACTIVE))
                .thenReturn(Collections.emptyList());
        List<Offer> result = offerService.getAllActiveOffers(1L);
        assertThat(result, empty());
    }

    @Test
    public void getAllActiveOffersReturnSortedListByPriceDesc() throws Exception {
        when(offerDao.findAllOffersByCarIdAndStatus(1L, ACTIVE))
                .thenReturn(Arrays.asList(
                        generateOfferEntity(1L, generatePersonEntity(), generateSalePropositionEntity(2L), 20.2),
                        generateOfferEntity(2L, generatePersonEntity(), generateSalePropositionEntity(2L), 20556.2),
                        generateOfferEntity(3L, generatePersonEntity(), generateSalePropositionEntity(2L), 10.2),
                        generateOfferEntity(4L, generatePersonEntity(), generateSalePropositionEntity(2L), 1000.2)
                ));
        List<Offer> result = offerService.getAllActiveOffers(1L);
        assertThat(result, hasSize(4));
        assertThat(Ordering.from(Comparator.comparing(Offer::getPrice)).reverse().isOrdered(result), is(true));
    }

    @Test(expected = ActiveOfferWithThisIdWasNotFoundException.class)
    public void acceptActiveOfferThrowsExceptionWhenActiveOfferWithIdWasNotFound() throws Exception {
        long offerId = 1L;
        when(offerDao.findFirstByIdAndStatus(offerId, ACTIVE))
                .thenReturn(Optional.empty());
        offerService.acceptActiveOffer(offerId);
    }

    @Test
    public void acceptActiveOfferReturnNewPerson() throws Exception {
        long offerId = 1L;
        long ownerId = 1L;
        long buyerId = 2L;

        OfferEntity offer = generateOfferEntityForOfferActivationTesting(
                buyerId,
                ownerId,
                OPEN,
                ACTIVE,
                ACTIVE
        );
        when(offerDao.findFirstByIdAndStatus(offerId, ACTIVE))
                .thenReturn(Optional.of(offer));

        offer.setStatus(ACCEPTED);
        offer.getSale().setStatus(CLOSED);
        offer.getSale().getCar().setOwner(offer.getBuyer());
        offer.getSale().getOffers().stream()
                .filter(o -> o.getStatus() != ACCEPTED)
                .forEach(o -> o.setStatus(DECLINED));

        when(offerDao.save(offer))
                .thenReturn(offer);

        Person result = offerService.acceptActiveOffer(offerId);
        assertThat(result, is(generatePersonWithId(2L)));
    }

    OfferEntity generateOfferEntityForOfferActivationTesting(Long buyerId, Long ownerId, SalePropositionEntity.Status saleStatus, OfferEntity.Status firstOfferStatus, OfferEntity.Status secondOfferStatus) {
        PersonEntity owner = generatePersonEntity();
        owner.setId(ownerId);

        PersonEntity buyer = generatePersonEntity();
        buyer.setId(buyerId);

        CarEntity car = generateCarEntity(1L, owner, "AA3295BB");
        SalePropositionEntity saleProp = generateSalePropositionEntity(1L);
        saleProp.setCar(car);
        saleProp.setStatus(saleStatus);

        OfferEntity firstOffer = generateOfferEntity(1L, buyer, saleProp, 2001.4);
        firstOffer.setStatus(firstOfferStatus);

        OfferEntity secondOffer = generateOfferEntity(2L, generatePersonEntity(), saleProp, 2001.4);
        secondOffer.setStatus(secondOfferStatus);

        saleProp.setOffers(Sets.newHashSet(firstOffer, secondOffer));

        return firstOffer;
    }

    private SalePropositionEntity generateSalePropositionEntity(long id) {
        SalePropositionEntity spe = new SalePropositionEntity();
        spe.setId(id);
        spe.setPrice(2001.1);
        spe.setStatus(OPEN);
        spe.setCar(new CarEntity());
        spe.setOffers(Collections.emptySet());
        return spe;
    }

    private PersonEntity generatePersonEntity() {
        PersonEntity pe = new PersonEntity();
        pe.setFirstName("firstName");
        pe.setLastName("lastName");
        pe.setPhoneNumber("1234567");
        return pe;
    }

    private Person generatePersonWithId(Long id) {
        return Person.builder()
                .id(id)
                .phoneNumber("1234567")
                .lastName("lastName")
                .firstName("firstName")
                .build();
    }

    private CarEntity generateCarEntity(Long id, PersonEntity owner, String number) {
        CarEntity ce = new CarEntity();
        ce.setId(id);
        ce.setBrand("BMW");
        ce.setColor("red");
        ce.setOwner(owner);
        ce.setPlateNumber(number);
        ce.setYear(2003);
        return ce;
    }

    private Offer generateOffer(Long id, Long salePropId, double price) {
        return Offer.builder()
                .id(id)
                .buyerPhoneNumber("1234567")
                .price(price)
                .buyerLastName("lastName")
                .buyerFirstName("firstName")
                .build();
    }

    private OfferEntity generateOfferEntity(Long id, PersonEntity personEntity, SalePropositionEntity salePropose, double price) {
        OfferEntity oe = new OfferEntity();
        oe.setId(id);
        oe.setBuyer(personEntity);
        oe.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        oe.setPrice(price);
        oe.setSale(salePropose);
        oe.setStatus(ACTIVE);
        return oe;
    }
}
