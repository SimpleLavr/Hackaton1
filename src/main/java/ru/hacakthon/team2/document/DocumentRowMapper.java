package ru.hacakthon.team2.document;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DocumentRowMapper implements RowMapper<Document> {
    @Override
    public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
        Document document = new Document();

        document.setDoctypeId(rs.getLong("doctype_id"));
        document.setId(rs.getLong("id"));
        document.setOriginal(rs.getString("original"));
        document.setChecked(rs.getBoolean("checked"));
        document.setFieldsValues(Arrays.stream((String[]) rs.getArray("fields_values").getArray()).collect(Collectors.toList()));

        return document;
    }
}
