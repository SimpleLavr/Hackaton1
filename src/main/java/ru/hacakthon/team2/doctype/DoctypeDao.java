package ru.hacakthon.team2.doctype;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.hacakthon.team2.utils.SqlUtils;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
public class DoctypeDao {

    private static final String TABLE_NAME = "document_types";

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    public void create(Doctype doctype) {
//        String columnNames = "";
//        for (String columnName : doctype.getFields()
//             ) {
//            columnNames += ", \"" + columnName + "\"" + " varchar(255) not null";
//        }
//
//        jdbcTemplate.execute("create table \"" + doctype.getName() +"\"(id serial primary key" + columnNames + ")");
//    }

    public void create(Doctype doctype) {

        String query = "insert into " + TABLE_NAME + "(name,original_url,fields) values(\'" + StringEscapeUtils.escapeSql(doctype.getName()) + "\'," +
               "\'" + doctype.getOriginalLocation() + "\'," + SqlUtils.toSqlArray(doctype.getFields()) + ");";
        System.out.print(query);
        jdbcTemplate.execute(query);
    }

    public List<Doctype> getAll() {
        return jdbcTemplate.query("select * from " + TABLE_NAME + ";", new DoctypeRowMapper());
    }

    public Doctype getById(Long id) {
        List<Doctype> resultList = jdbcTemplate.query("select * from " + TABLE_NAME + " where id=?;", new DoctypeRowMapper(), new Object[]{id});
        if(resultList.isEmpty()) return null;
        else return resultList.get(0);
    }

    public boolean deleteById(Long id) {
        return jdbcTemplate.update("delete from " + TABLE_NAME + " where id=?;", new Object[]{id}) > 0 ? true : false;
    }

}
