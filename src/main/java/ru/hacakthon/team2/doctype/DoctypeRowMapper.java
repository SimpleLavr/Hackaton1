package ru.hacakthon.team2.doctype;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DoctypeRowMapper implements RowMapper<Doctype> {
    @Override
    public Doctype mapRow(ResultSet rs, int rowNum) throws SQLException {
        Doctype doctype = new Doctype();
        doctype.setId(rs.getLong(1));
        doctype.setName(rs.getString(2));
        doctype.setFields(Arrays.stream((String[]) rs.getArray(3).getArray()).collect(Collectors.toList()));
        return doctype;
    }
}
