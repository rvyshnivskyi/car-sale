package com.playtika.sales.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.playtika.sales.dao.entity.CarEntity;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DBUnit(qualifiedTableNames = true, url = "jdbc:mysql://localhost:3306/car_sales_db_test?useSSL=false")
public class CarDaoTest extends AbstractDaoTest<CarDao> {

    @Test
    @DataSet(value = "two-cars-with-sales.xml")
    public void allCarsAreReturned() throws Exception {
        List<CarEntity> result = dao.findAll();
        assertThat(result, hasSize(2));
        assertThat(result.stream().map(CarEntity::getId).collect(Collectors.toList()), hasItems(2L, 4L));
        assertThat(result.stream().map(CarEntity::getPlateNumber).collect(Collectors.toList()), hasItems("AA3295BB", "AA3296BB"));
        assertThat(result.stream().map(CarEntity::getBrand).collect(Collectors.toList()), hasItems("BMW", "Audi"));
        assertThat(result.stream().map(CarEntity::getYear).collect(Collectors.toList()), hasItems(2003, 2009));
        assertThat(result.stream().map(CarEntity::getColor).collect(Collectors.toList()), hasItems("red", "yellow"));
    }

    @Test
    @DataSet(cleanBefore = true)
    public void findAllReturnEmptyListIfNoOneCarExist() throws Exception {
        List<CarEntity> result = dao.findAll();
        assertThat(result, empty());
    }
}
