package com.playtika.sales.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestExecutionListeners(listeners = {DbUnitTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class SalePropositionDaoTest extends AbstractDaoTest<SalePropositionDao> {

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    public void returnSaleDetailsByCarIdAndStatus() throws Exception {
        List<SalePropositionEntity> saleProp = dao.findByCarIdAndStatus(2L, SalePropositionEntity.Status.OPEN);
        assertThat(saleProp.get(0).getId(), is(5L));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    public void emptyListReturnedWhenNoOneOpenSaleForCar() throws Exception {
        List<SalePropositionEntity> result1 = dao.findByCarIdAndStatus(6L, SalePropositionEntity.Status.OPEN);
        List<SalePropositionEntity> result2 = dao.findByCarIdAndStatus(2L, SalePropositionEntity.Status.CLOSED);
        assertThat(result1, empty());
        assertThat(result2, empty());
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    @ExpectedDatabase(value = "/datasets/cars-with-sales-after-saving.xml")
    @Commit
    public void cascadeSavingOfCarDataAndSalesDetails() throws Exception {
        PersonEntity owner = new PersonEntity();
                owner.setCity("Vinnitsa");
                owner.setPhoneNumber("228");
                owner.setFirstName("Petia");
                owner.setLastName("Poroh");

        CarEntity car = new CarEntity();
                car.setOwner(owner);
                car.setBrand("Opel");
                car.setColor("blue");
                car.setPlateNumber("AA3297BB");
                car.setYear(2014);

        SalePropositionEntity saleProp = new SalePropositionEntity();
                saleProp.setCar(car);
                saleProp.setStatus(SalePropositionEntity.Status.OPEN);
                saleProp.setPrice(3000);

        SalePropositionEntity savedProp = dao.save(saleProp);

        assertThat(savedProp.getId(), is(7L));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    @ExpectedDatabase(value = "/datasets/cars-with-sales-after-deleting.xml")
    @Commit
    public void deleteByCarIdAndStatusReturnOneIfSuccess() throws Exception {
        int result = dao.deleteByCarIdAndStatus(4L, SalePropositionEntity.Status.OPEN);
        assertThat(result, is(1));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    @ExpectedDatabase(value = "/datasets/two-cars-with-sales.xml")
    @Commit
    public void deleteByCarIdAndStatusReturnZeroIfUnsuccess() throws Exception {
        int result = dao.deleteByCarIdAndStatus(4L, SalePropositionEntity.Status.CLOSED);
        assertThat(result, is(0));
    }
}
