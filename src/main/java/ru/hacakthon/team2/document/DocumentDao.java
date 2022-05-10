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

        StringBuilder query = new StringBuilder().append("insert into documents(doctype_id,original,fields_values,checked,changed) values(")
                .append(doctypeId + ",")
                .append("\'" + original + "\',")
                .append(SqlUtils.toSqlArray(fieldValues) + ",false,false);");
        System.out.println(query.toString());
        jdbcTemplate.execute(query.toString());
    }

    public List<Document> getAllByDoctype(Long doctypeId) throws Exception {
        Doctype doctype = doctypeDao.getById(doctypeId);
        if(doctype == null) throw new Exception("No doctype with this id");

        List<Document> documentList = jdbcTemplate.query("select * from " + TABLE_NAME + " where doctype_id = " + doctypeId + ";", new DocumentRowMapper());

        return documentList;
    }

    public Document getById(Long id) {
        List<Document> result = jdbcTemplate.query("select * from " + TABLE_NAME + " where id=" + id + ";", new DocumentRowMapper());
        if(result.isEmpty()) return null;
        return result.get(0);
    }

    public boolean update(Document document) {
        StringBuilder query = new StringBuilder();
        query.append("update ").append(TABLE_NAME).append(" set original = \'").append(document.getOriginal())
                .append("\',checked = ").append(document.isChecked())
                .append(",changed = ").append(document.isChanged())
                .append(",fields_values = ").append(SqlUtils.toSqlArray(document.getFieldsValues()))
                .append(" where id = ").append(document.getId()).append(";");
        return jdbcTemplate.update(query.toString()) > 0 ? true : false;
    }

    public boolean delete(Long id) {
        StringBuilder query = new StringBuilder();
        query.append("delete from").append(TABLE_NAME)
                .append(" where id = ").append(id).append(";");
        return jdbcTemplate.update(query.toString()) > 0 ? true : false;
    }

    public Long getDocumentByValues(List<String> values) {
        StringBuilder query = new StringBuilder();

        query.append("select id from ").append(TABLE_NAME)
                .append(" where fields_values = ").append(SqlUtils.toSqlArray(values))
                .append(";");

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(query.toString());
        if(!resultSet.next()) return null;

        return resultSet.getLong("id");
    }

    public int getDocumentNumber(Long doctypeId) {
        StringBuilder query = new StringBuilder();
        query.append("select count(id) from ").append(TABLE_NAME)
                .append(";");

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(query.toString());
        resultSet.next();
        return resultSet.getInt("count");
    }

    public int getChangedDocumentsNumber(Long doctypeId) {
        StringBuilder query = new StringBuilder();
        query.append("select count(id) from ").append(TABLE_NAME)
                .append(" where changed = true;");

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(query.toString());
        resultSet.next();
        return resultSet.getInt("count");
    }

    public int getCheckedDocumentsNumber(Long doctypeId) {
        StringBuilder query = new StringBuilder();
        query.append("select count(id) from ").append(TABLE_NAME)
                .append(" where checked = true");

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(query.toString());
        resultSet.next();
        return resultSet.getInt("count");
    }

    public int getCheckedAndChangedDocumentsNumber(Long doctypeId) {
        StringBuilder query = new StringBuilder();
        query.append("select count(id) from ").append(TABLE_NAME)
                .append(" where checked = true and changed = true");

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(query.toString());
        resultSet.next();
        return resultSet.getInt("count");
    }
}
