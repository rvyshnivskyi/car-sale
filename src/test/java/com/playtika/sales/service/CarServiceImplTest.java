package com.playtika.sales.service;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.Date;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceImplTest {

    @Mock
    CarDao carDao;

    @Mock
    OfferDao offerDao;

    @Mock
    SalePropositionDao propositionDao;

    CarService carService;

    @Before
    public void setUp() throws Exception {
        carService = new CarServiceImpl(carDao, propositionDao, offerDao);
    }

    @Test
    public void addCarReturnInsertedCar() {
        long carId = 1L;
        String carNumber = "aa1";
        when(propositionDao.save(notNull(SalePropositionEntity.class)))
                .thenReturn(generatePropEntity(generateCarEntity(carId, new PersonEntity(), carNumber)));

        Car result = carService.addCarForSale(generateCar(null, "aa1"), generateSaleDetails(carId));
        assertThat(result, is(generateCar(carId, carNumber)));
    }

    @Test(expected = DuplicateCarSaleDetailsException.class)
    public void addCarThrowsExceptionWhenCarDuplicated() {
        long carId = 1L;
        String carNumber = "aa1";
        when(propositionDao.save(notNull(SalePropositionEntity.class)))
                .thenReturn(generatePropEntity(generateCarEntity(carId, new PersonEntity(), carNumber)))
                .thenThrow(DataIntegrityViolationException.class);

        carService.addCarForSale(generateCar(null, "aa1"), generateSaleDetails(carId));
        carService.addCarForSale(generateCar(null, "aa1"), generateSaleDetails(carId));
    }

    @Test
    public void carOwnerSuccessfullyReturned() throws Exception {
        Long carId = 1L;
        when(carDao.findOne(carId))
                .thenReturn(generateCarEntity(carId, generatePersonEntity(), "AA3265"));
        Person result = carService.getCarOwner(carId);
        assertThat(result, is(generatePersonWithId(null)));
    }

    @Test(expected = CarWasNotFoundException.class)
    public void getCarOwnerReturnedExceptionWhenCarNotExist() throws Exception {
        Long carId = 1L;
        when(carDao.findOne(carId))
                .thenReturn(null);
        carService.getCarOwner(carId);
    }

    @Test
    public void addOfferReturnInsertedOffer() throws Exception {
        when(propositionDao.findByCarIdAndStatus(1L, SalePropositionEntity.Status.OPEN))
                .thenReturn(Arrays.asList(generateSalePropositionEntity(2L)));
        when(offerDao.save(notNull(OfferEntity.class)))
                .thenReturn(generateOfferEntity(1L, generatePersonEntity(), generateSalePropositionEntity(2L), 222.1));
        Offer result = carService.addOfferForSalePropose(generateOffer(null, null, 222.1), 1L);
        assertThat(result, is(generateOffer(1L, 2L, 222.1)));
    }

    @Test(expected = SaleProposeNotFoundForThisCarException.class)
    public void addOfferThrowsExceptionWhenNoSalePropsForThisCar() throws Exception {
        when(propositionDao.findByCarIdAndStatus(1L, SalePropositionEntity.Status.OPEN))
                .thenReturn(emptyList());
;
        carService.addOfferForSalePropose(generateOffer(null, null, 222.1), 1L);
    }

    @Test
    public void getAllActiveOffersReturnOffersList() throws Exception {
        when(offerDao.findAllOffersByCarIdAndStatus(1L, OfferEntity.Status.ACTIVE))
                .thenReturn(Arrays.asList(
                        generateOfferEntity(1L, generatePersonEntity(), generateSalePropositionEntity(2L), 12.3),
                        generateOfferEntity(2L, generatePersonEntity(), generateSalePropositionEntity(2L), 123.4)));
        List<Offer> result = carService.getAllActiveOffers(1L);
        assertThat(result, hasSize(2));
        assertThat(result, hasItems(generateOffer(1L, 2L, 12.3), generateOffer(2L, 2L, 123.4)));
    }

    @Test
    public void getAllActiveOffersReturnEmptyListIfNoOneOffersForCarExist() throws Exception {
        when(offerDao.findAllOffersByCarIdAndStatus(1L, OfferEntity.Status.ACTIVE))
                .thenReturn(Collections.emptyList());
        List<Offer> result = carService.getAllActiveOffers(1L);
        assertThat(result, empty());
    }

    @Test
    public void getAllActiveOffersReturnSortedListByPriceDesc() throws Exception {
        when(offerDao.findAllOffersByCarIdAndStatus(1L, OfferEntity.Status.ACTIVE))
                .thenReturn(Arrays.asList(
                        generateOfferEntity(1L, generatePersonEntity(), generateSalePropositionEntity(2L), 20.2),
                        generateOfferEntity(2L, generatePersonEntity(), generateSalePropositionEntity(2L), 20556.2),
                        generateOfferEntity(3L, generatePersonEntity(), generateSalePropositionEntity(2L), 10.2),
                        generateOfferEntity(4L, generatePersonEntity(), generateSalePropositionEntity(2L), 1000.2)
                ));
        List<Offer> result = carService.getAllActiveOffers(1L);
        assertThat(result, hasSize(4));
        assertThat(Ordering.from(Comparator.comparing(Offer::getPrice)).reverse().isOrdered(result), is(true));
    }

    @Test(expected = ActiveOfferWithThisIdWasNotFoundException.class)
    public void acceptActiveOfferThrowsExceptionWhenActiveOfferWithIdWasNotFound() throws Exception {
        long offerId = 1L;
        when(offerDao.findFirstByIdAndStatus(offerId, OfferEntity.Status.ACTIVE))
                .thenReturn(Optional.empty());
        carService.acceptActiveOffer(offerId);
    }

    @Test
    public void acceptActiveOfferReturnNewPerson() throws Exception {
        long offerId = 1L;
        long ownerId = 1L;
        long buyerId = 2L;

        OfferEntity offer = generateOfferEntityForOfferActivationTesting(
                buyerId,
                ownerId,
                SalePropositionEntity.Status.OPEN,
                OfferEntity.Status.ACTIVE,
                OfferEntity.Status.ACTIVE
        );
        when(offerDao.findFirstByIdAndStatus(offerId, OfferEntity.Status.ACTIVE))
                .thenReturn(Optional.of(offer));

        offer.setStatus(OfferEntity.Status.ACCEPTED);
        offer.getSale().setStatus(SalePropositionEntity.Status.CLOSED);
        offer.getSale().getCar().setOwner(offer.getBuyer());
        offer.getSale().getOffers().stream()
                .filter(o -> o.getStatus() != OfferEntity.Status.ACCEPTED)
                .forEach(o -> o.setStatus(OfferEntity.Status.DECLINED));

        when(offerDao.save(offer))
                .thenReturn(offer);

        Person result = carService.acceptActiveOffer(offerId);
        assertThat(result, is(generatePersonWithId(2L)));
    }

    @Test
    public void getAllCarsReturnCarList() {
        CarEntity firstCarEntity = generateCarEntity(1L, new PersonEntity(), "11");
        CarEntity secondCarEntity = generateCarEntity(2L, new PersonEntity(), "22");

        when(carDao.findAll()).thenReturn(Arrays.asList(firstCarEntity, secondCarEntity));
        List<Car> result = carService.getAllCars();
        assertThat(result, hasSize(2));
        assertThat(result, hasItems(is(generateCar(1L, "11")), is(generateCar(2L, "22"))));
    }

    @Test
    public void getCarSaleWithNotExistId() {
        long carId = 0L;
        when(propositionDao.findByCarIdAndStatus(carId, SalePropositionEntity.Status.OPEN))
                .thenReturn(emptyList());
        Optional<SaleDetails> result = carService.getSaleDetails(carId);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void getCarSaleReturnSaleDetails() {
        long carId = 1L;
        CarEntity returnedCarEntity = generateCarEntity(carId, generatePersonEntity(), "11");

        when(propositionDao.findByCarIdAndStatus(carId, SalePropositionEntity.Status.OPEN))
                .thenReturn(Arrays.asList(generatePropEntity(returnedCarEntity)));
        Optional<SaleDetails> result = carService.getSaleDetails(carId);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(generateSaleDetails(carId)));
    }

    @Test
    public void deleteCarDetailsReturnFalseWithNotExistCarId() {
        long carId = 2L;
        when(propositionDao.deleteByCarIdAndStatus(carId, SalePropositionEntity.Status.OPEN))
                .thenReturn(0);
        boolean result = carService.deleteSaleDetails(carId);
        assertThat(result, is(false));
    }

    @Test
    public void deleteCarDetailsReturnTrueWithExistCarId() {
        long carId = 2L;
        when(propositionDao.deleteByCarIdAndStatus(carId, SalePropositionEntity.Status.OPEN))
                .thenReturn(1);
        boolean result = carService.deleteSaleDetails(carId);
        assertThat(result, is(true));
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
        spe.setStatus(SalePropositionEntity.Status.OPEN);
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

    private SaleDetails generateSaleDetails(Long carId) {
        return SaleDetails.builder()
                .carId(carId)
                .price(123.4)
                .ownerFirstName("firstName")
                .ownerPhoneNumber("1234567")
                .ownerLastName("lastName").build();
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

    private SalePropositionEntity generatePropEntity(CarEntity car) {
        SalePropositionEntity propositionEntity = new SalePropositionEntity();
            propositionEntity.setPrice(123.4);
            propositionEntity.setCar(car);
        return propositionEntity;
    }

    private Car generateCar(Long id, String number) {
        return Car.builder()
                .id(id)
                .color("red")
                .number(number)
                .brand("BMW")
                .year(2003).build();
    }

    private Offer generateOffer(Long id, Long salePropId, double price) {
        return Offer.builder()
                .id(id)
                .saleProposeId(salePropId)
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
        oe.setStatus(OfferEntity.Status.ACTIVE);
        return oe;
    }
}
