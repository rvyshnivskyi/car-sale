package com.playtika.sales.dao;

import com.github.database.rider.core.DBUnitRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractDaoTest<D> {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(() -> {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        return connection;
    });

    @Autowired
    protected D dao;

    @Before
    public void setUp() throws Exception {
        System.out.println("sadsda");
    }
}
