package com.playtika.sales.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SalePropositionDaoTest extends AbstractDaoTest<SalePropositionDao> {

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    public void returnSaleDetailsByCarIdAndStatus() throws Exception {
        List<SalePropositionEntity> saleProp = dao.findByCar_IdAndStatus(2L, SalePropositionEntity.Status.OPEN);
        assertThat(saleProp.stream().findFirst().get().getId(), is(5L));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    public void emptyListReturnedWhenNoOneOpenSaleForCar() throws Exception {
        List<SalePropositionEntity> result1 = dao.findByCar_IdAndStatus(6L, SalePropositionEntity.Status.OPEN);
        List<SalePropositionEntity> result2 = dao.findByCar_IdAndStatus(2L, SalePropositionEntity.Status.CLOSED);
        assertThat(result1, empty());
        assertThat(result2, empty());
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    @ExpectedDatabase(value = "/datasets/cars-with-sales-after-saving.xml")
    @Commit
    public void cascadeSavingOfCarDataAndSalesDetails() throws Exception {
        PersonEntity owner = PersonEntity.builder()
                .city("Vinnitsa")
                .phoneNumber("228")
                .firstName("Petia")
                .lastName("Poroh")
                .build();
        CarEntity car = CarEntity.builder()
                .owner(owner)
                .brand("Opel")
                .color("blue")
                .plateNumber("AA3297BB")
                .year(2014)
                .build();
        SalePropositionEntity saleProp = new SalePropositionEntity();
        saleProp.setCar(car);
        saleProp.setStatus(SalePropositionEntity.Status.OPEN);
        saleProp.setPrice(3000);
        SalePropositionEntity savedProp = dao.save(saleProp);
        saleProp.setId(7L);
        assertThat(savedProp, equalTo(saleProp));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    @ExpectedDatabase(value = "/datasets/cars-with-sales-after-deleting.xml")
    @Commit
    public void deleteByCarIdAndStatusReturnOneIfSuccess() throws Exception {
        int result = dao.deleteByCar_IdAndStatus(4L, SalePropositionEntity.Status.OPEN);
        assertThat(result, is(1));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    @ExpectedDatabase(value = "/datasets/two-cars-with-sales.xml")
    @Commit
    public void deleteByCarIdAndStatusReturnZeroIfUnsuccess() throws Exception {
        int result = dao.deleteByCar_IdAndStatus(4L, SalePropositionEntity.Status.CLOSED);
        assertThat(result, is(0));
    }
}
