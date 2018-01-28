package com.playtika.sales.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.playtika.sales.dao.entity.OfferEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static com.playtika.sales.dao.entity.OfferEntity.Status.*;
import static com.playtika.sales.dao.entity.SalePropositionEntity.Status.CLOSED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OfferDaoTest extends AbstractDaoTest<OfferDao> {

    @Test
    @DatabaseSetup("/datasets/two-cars-with-sales-and-four-offers.xml")
    public void findAllActiveOffersByCarIdReturnEmptyListWhenNoOffers() throws Exception {
        List<OfferEntity> result = dao.findAllOffersByCarIdAndStatus(1L, ACTIVE);
        assertThat(result, empty());
    }

    @Test
    @DatabaseSetup("/datasets/two-cars-with-sales-and-four-offers.xml")
    public void offersPerCarSuccessfullyReturned() throws Exception {
        List<OfferEntity> result = dao.findAllOffersByCarIdAndStatus(2, ACTIVE);
        assertThat(result, hasSize(2));
        assertThat(result, hasItems(hasProperty("id", is(1L)), hasProperty("id", is(2L))));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales-after-adding-offer.xml")
    public void offerByIdAndStatusCanReturnEmptyOptional() throws Exception {
        Optional<OfferEntity> result1 = dao.findFirstByIdAndStatus(2L, ACTIVE);
        Optional<OfferEntity> result2 = dao.findFirstByIdAndStatus(1L, DECLINED);
        assertThat(result1.isPresent(), is(false));
        assertThat(result2.isPresent(), is(false));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-two-active-offers.xml")
    @ExpectedDatabase(value = "/datasets/two-cars-with-two-active-offers-after-acception-offer.xml")
    @Commit
    public void offerSuccessfullyAcceptedAndDataModified() throws Exception {
        OfferEntity offer = dao.findFirstByIdAndStatus(1L, ACTIVE).get();
        offer.setStatus(ACCEPTED);
        offer.getSale().setStatus(CLOSED);
        offer.getSale().getCar().setOwner(offer.getBuyer());
        offer.getSale().getOffers().stream()
                .filter(o -> o.getStatus() != ACCEPTED)
                .forEach(o -> o.setStatus(DECLINED));
        dao.save(offer);
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    @ExpectedDatabase(value = "/datasets/two-cars-with-sales-after-adding-offer.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @Commit
    public void offerSuccessfullySaves() throws Exception {
        PersonEntity pe = new PersonEntity();
        pe.setPhoneNumber("123456");
        pe.setLastName("Dyakon");
        pe.setFirstName("Irina");

        OfferEntity oe = new OfferEntity();
        oe.setBuyer(pe);
        oe.setDate(Date.valueOf("1995-2-23"));
        oe.setPrice(100.1);

        dao.save(oe);
    }
}