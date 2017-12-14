package com.playtika.sales.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class SalePropositionDaoTest extends AbstractDaoTest<SalePropositionDao> {

    @Test
    @DataSet(value = "two-cars-with-sales.xml")
    public void returnSaleDetailsByCarIdAndStatus() throws Exception {
        List<SalePropositionEntity> saleProp = dao.findByCarIdAndStatus(2L, SalePropositionEntity.Status.OPEN);
        assertThat(saleProp.stream().findFirst().get().getId(), is(5L));
    }

    @Test
    @DataSet(value = "two-cars-with-sales.xml")
    public void emptyListReturnedWhenNoOneOpenSaleForCar() throws Exception {
        List<SalePropositionEntity> result1 = dao.findByCarIdAndStatus(6L, SalePropositionEntity.Status.OPEN);
        List<SalePropositionEntity> result2 = dao.findByCarIdAndStatus(2L, SalePropositionEntity.Status.CLOSED);
        assertThat(result1, empty());
        assertThat(result2, empty());
    }

    @Test
    @DataSet(value = "two-cars-with-sales.xml")
    @ExpectedDataSet("cars-with-sales-after-saving.xml")
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
        dao.save(saleProp);
    }

    @Test
    @DataSet(value = "two-cars-with-sales.xml")
    public void findByCarIdAndStatusReturnsSalesProp() throws Exception {

    }
}
