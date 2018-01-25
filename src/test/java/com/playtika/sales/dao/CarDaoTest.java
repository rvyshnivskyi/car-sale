package com.playtika.sales.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.playtika.sales.dao.entity.CarEntity;
import com.playtika.sales.dao.entity.PersonEntity;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CarDaoTest extends AbstractDaoTest<CarDao> {

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    public void allCarsAreReturned() throws Exception {
        List<CarEntity> result = dao.findAll();
        assertThat(result, hasSize(2));
        assertThat(result, hasItems(
                allCarEntityPropertiesMatcher(2L, "AA3295BB", "BMW", 2003, "red"),
                allCarEntityPropertiesMatcher(4L, "AA3296BB", "Audi", 2009, "yellow")
        ));
    }

    @Test(expected = DataIntegrityViolationException.class)
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    public void addCarThrowExceptionWhenCarPlateNumberDuplicates() throws Exception {
        CarEntity ce = new CarEntity();
        ce.setBrand("Volvo");
        ce.setYear(1995);
        ce.setColor("blue");
        ce.setPlateNumber("AA3295BB");
        dao.save(ce);
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    public void carOwnerSuccessfullyReturned() throws Exception {
        PersonEntity owner = dao.findOne(2L).getOwner();
        assertThat(owner, allOf(hasProperty("id", is(13L)), hasProperty("firstName", is("Ira"))));
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml")
    public void findOneReturnedNullIfCarNotExist() throws Exception {
        CarEntity car = dao.findOne(3L);
        assertThat(car, nullValue());
    }

    @Test
    @DatabaseSetup(value = "/datasets/two-cars-with-sales.xml", type = DatabaseOperation.DELETE_ALL)
    public void findAllReturnEmptyListIfNoOneCarExist() throws Exception {
        List<CarEntity> result = dao.findAll();
        assertThat(result, empty());
    }

    Matcher<Object> allCarEntityPropertiesMatcher(Long id, String plateNumber, String brand, int year, String color) {
        return allOf(
                hasProperty("id", is(id)),
                hasProperty("plateNumber", is(plateNumber)),
                hasProperty("brand", is(brand)),
                hasProperty("year", is(year)),
                hasProperty("color", is(color)));
    }
}
