package ru.hacakthon.team2.document;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;
import ru.hacakthon.team2.utils.SqlUtils;

import java.sql.ResultSet;
import java.util.List;

@Component
public class DocumentDao {

    private final String TABLE_NAME = "documents";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DoctypeDao doctypeDao;

    public void create(List<String> fieldValues, Long doctypeId, String original) {

        StringBuilder query = new StringBuilder().append("insert into documents(doctype_id,original,fields_values,checked) values(")
                .append(doctypeId + ",")
                .append("\'" + original + "\',")
                .append(SqlUtils.toSqlArray(fieldValues) + ",false);");
        System.out.println(query.toString());
        jdbcTemplate.execute(query.toString());
    }

    public List<Document> getAllByDoctype(Long doctypeId) throws Exception {
        Doctype doctype = doctypeDao.getById(doctypeId);
        if(doctype == null) throw new Exception("No doctype with this id");

        List<Document> documentList = jdbcTemplate.query("select * from " + TABLE_NAME + " where doctype_id = " + doctypeId + ";", new DocumentRowMapper());

        return documentList;
    }

    Document getById(Long id) {
        List<Document> result = jdbcTemplate.query("select * from " + TABLE_NAME + " where id=" + id + ";", new DocumentRowMapper());
        if(result.isEmpty()) return null;
        return result.get(0);
    }

    public boolean update(Document document) {
        StringBuilder query = new StringBuilder();
        query.append("update ").append(TABLE_NAME).append(" set original = \'").append(document.getOriginal())
                .append("\',checked = ").append(document.isChecked())
                .append(",fields_values = ").append(SqlUtils.toSqlArray(document.getFieldsValues()))
                .append(" where id = ").append(document.getId()).append(";");
        return jdbcTemplate.update(query.toString()) > 0 ? true : false;
    }
}
