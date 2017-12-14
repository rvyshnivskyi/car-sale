package com.playtika.sales.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.playtika.sales.dao.entity.CarEntity;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CarDaoTest extends AbstractDaoTest<CarDao> {

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
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
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml", type = DatabaseOperation.DELETE_ALL)
    public void findAllReturnEmptyListIfNoOneCarExist() throws Exception {
        List<CarEntity> result = dao.findAll();
        assertThat(result, empty());
    }
}
