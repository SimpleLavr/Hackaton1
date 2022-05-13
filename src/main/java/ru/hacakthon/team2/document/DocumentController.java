package ru.hacakthon.team2.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hacakthon.team2.changes.ChangesDao;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;
import ru.hacakthon.team2.utils.SqlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/document")
@CrossOrigin
public class DocumentController {

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private DoctypeDao doctypeDao;

    @Autowired
    private ChangesDao changesDao;

    @GetMapping
    public ResponseEntity getDocumentsByDoctype(@RequestParam(required = true) Long doctypeId,
                                                @RequestParam(required = false) boolean getOriginal) throws Exception {
        Doctype doctype = doctypeDao.getById(doctypeId);

        if(doctype == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No doctype with id " + doctypeId);

        List<Document> documentList;

        try {
            documentList = documentDao.getAllByDoctype(doctypeId);
        } catch(DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request failed: " + e.getMessage());
        }

        JSONArray documentArray = new JSONArray();

        for(Document document : documentList) {

            List<String> updatedFieldsValues = changesDao.getValuesListByDocumentId(document.getId());
            if(updatedFieldsValues != null && !getOriginal) document.setFieldsValues(updatedFieldsValues);

            documentArray.add(DocumentUtils.documentToJson(document, doctype));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(documentArray.toJSONString());
    }

    @GetMapping("/{id}")
    public ResponseEntity getDocumentById(@PathVariable Long id, @RequestParam(required = false) boolean getOriginal) throws Exception {

        Document document;

        try {
            document = documentDao.getById(id);
        } catch(DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request failed: " + e.getMessage());
        }

        if(document == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No document with id " + id);

        List<String> updatedFieldsValues = changesDao.getValuesListByDocumentId(id);

        if(updatedFieldsValues != null && !getOriginal) document.setFieldsValues(updatedFieldsValues);

        Doctype doctype = doctypeDao.getById(document.getDoctypeId());

        JSONObject jsonDocument = DocumentUtils.documentToJson(document, doctype);
        jsonDocument.put("doctypeName", doctype.getName());

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonDocument.toJSONString());
    }

    @PostMapping("/{id}")
    public ResponseEntity updateDocument(@PathVariable Long id, @RequestBody String jsonDocumentString) throws Exception {
//        System.out.println(jsonDocumentString);

        JSONObject jsonDocument = (JSONObject) JSONValue.parse(jsonDocumentString);

        Document documentToUpdate = documentDao.getById(id);

        if(documentToUpdate == null) return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body("No document with id " + id + " found");

//        System.out.println(SqlUtils.getChecksum(documentToUpdate) + "\n"
//        + Long.valueOf(jsonDocument.get("checksum").toString()) + "\n" +
//                SqlUtils.getChecksum(documentToUpdate).equals(Long.valueOf(jsonDocument.get("checksum").toString())));

        List<String> updatedFieldsValues = changesDao.getValuesListByDocumentId(id);

        if(updatedFieldsValues != null) documentToUpdate.setFieldsValues(updatedFieldsValues);

        if(!SqlUtils.getChecksum(documentToUpdate).equals(Long.valueOf(jsonDocument.get("checksum").toString())))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update request for document " + documentToUpdate.getId() +
                    " failed: checksums do not match. Please refresh document");

        //Снова получаем документ из базы, т.к. оригинальные значения полей были заменены
        documentToUpdate = documentDao.getById(id);

        Doctype doctype = doctypeDao.getById(documentToUpdate.getDoctypeId());

        documentToUpdate.setChecked(Boolean.valueOf(jsonDocument.get("checked").toString()));
//        documentToUpdate.setOriginal(jsonDocument.get("original").toString().replace(doctype.getOriginalLocation(), ""));

        List<String> fieldNames = doctype.getFields();

        List<String> newFieldValues = new ArrayList<>();

        for(String fieldName : fieldNames) {
            String fieldValue = jsonDocument.get(fieldName).toString();

            if(fieldValue == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No field " + fieldName + " found");

            newFieldValues.add(fieldValue);
        }
//        documentToUpdate.setFieldsValues(newFieldValues);



        boolean updated;
        try {
            if(!documentToUpdate.getFieldsValues().equals(newFieldValues)) {
                documentToUpdate.setChanged(true);
                changesDao.createOrUpdate(newFieldValues, id);
            }
            updated = documentDao.update(documentToUpdate);
        } catch(DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request failed: " + e.getMessage());
        }

        if(updated) return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Document with id " + id + " successfully updated");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update failed");
    }
}
