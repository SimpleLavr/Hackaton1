package ru.hacakthon.team2.document;

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
    JdbcTemplate jdbcTemplate;

    @Autowired
    DoctypeDao doctypeDao;

    public void create(List<String> fieldValues, Long doctypeId, String original) {

        StringBuilder query = new StringBuilder().append("insert into documents(doctype_id,original,fields_values) values(")
                .append(doctypeId + ",")
                .append("\'" + original + "\',")
                .append(SqlUtils.toSqlArray(fieldValues) + ");");
        System.out.println(query.toString());
        jdbcTemplate.execute(query.toString());
    }

    public String getAllByDoctypeInJson(Long doctypeId) throws Exception {
        Doctype doctype = doctypeDao.getById(doctypeId);
        if(doctype == null) throw new Exception("No doctype with this id");

        List<Document> documentList = jdbcTemplate.query("select * from ? where doctype_id=?", new DocumentRowMapper(), new Object[]{TABLE_NAME, doctypeId});

        StringBuilder json = new StringBuilder().append("[");

        for (Document document : documentList
             ) {
            json.append(",").append(documentToJson(document,doctype));
        }
        json.deleteCharAt(1);
        return json.toString();
    }

    Document getById(Long id) {
        List<Document> result = jdbcTemplate.query("select * from ? whre id=", new DocumentRowMapper(), new Object[]{TABLE_NAME,id});
        if(result.isEmpty()) return null;
        return result.get(0);
    }

    String getJsonById(Long id) {
        List<Document> result = jdbcTemplate.query("select * from ? whre id=", new DocumentRowMapper(), new Object[]{TABLE_NAME,id});
        if(result.isEmpty()) return "{}";

        Document document = result.get(0);
        Doctype doctype = doctypeDao.getById(document.getDoctypeId());

        try {
            return documentToJson(document,doctype);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    String documentToJson(Document document, Doctype doctype) throws Exception {
        if(doctype.getId() != document.getDoctypeId()) throw new Exception("Document has other doctype");
        StringBuilder json = new StringBuilder();
        List<String> namesList = doctype.getFields();
        List<String> valueList = document.getFieldsValues();

        json.append("{\"id\":\"").append(document.getId()).append("\",\"original\":\"")
                .append(doctype.getOriginalLocationUrl()).append(document.getOriginal()).append("\",\"checked\":")
                .append(document.isChecked());

        for(int i = 0; i < valueList.size(); i++) {
            json.append(",\"").append(namesList.get(i)).append("\":")
                    .append("\"").append(valueList.get(i)).append("\"");
        }
        json.append("}");

        return json.toString();
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
