package ru.hacakthon.team2.doctype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DoctypeDao {

    @Autowired
    JdbcTemplate jdbcTemplate;


}
