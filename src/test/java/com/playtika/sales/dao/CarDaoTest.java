package com.playtika.sales.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.playtika.sales.dao.entity.CarEntity;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestExecutionListeners(listeners = {DbUnitTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
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
