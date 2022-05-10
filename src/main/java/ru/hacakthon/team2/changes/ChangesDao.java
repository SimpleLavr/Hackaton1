package ru.hacakthon.team2.changes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.hacakthon.team2.document.DocumentDao;
import ru.hacakthon.team2.utils.SqlUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChangesDao {

    private final String TABLE_NAME = "changes";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DocumentDao documentDao;

    private Statement statement;

    @PostConstruct
    public void init() throws SQLException {
        this.statement = jdbcTemplate.getDataSource().getConnection().createStatement();
    }

    @PreDestroy
    public void destroy() throws SQLException {
        statement.close();
    }

    public void createOrUpdate(List<String> fieldsValues, Long documentId) {
        if(getValuesListByDocumentId(documentId)  == null)
            create(fieldsValues, documentId);
        else
            update(fieldsValues, documentId);
    }

    public void create(List<String> fieldsValues, Long documentId) {
        StringBuilder query = new StringBuilder();

        query
                .append("insert into ")
                .append(TABLE_NAME)
                .append("(document_id,fields_values) values(")
                .append(documentId)
                .append(",")
                .append(SqlUtils.toSqlArray(fieldsValues))
                .append(");");

        jdbcTemplate.execute(query.toString());
    }

    public void update(List<String> fieldsValues, Long documentId) {

        StringBuilder query = new StringBuilder();
        query
                .append("update ")
                .append(TABLE_NAME)
                .append(" set fields_values = ")
                .append(SqlUtils.toSqlArray(fieldsValues))
                .append(" where document_id = ")
                .append(documentId)
                .append(";");

        jdbcTemplate.update(query.toString());
    }

    public List<String> getValuesListByDocumentId(Long documentId) {
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery("select fields_values from " + TABLE_NAME + " where document_id = " + documentId + ";");
            if(!resultSet.next()) return null;
            return Arrays.stream((String[]) resultSet.getArray("fields_values").getArray()).collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
