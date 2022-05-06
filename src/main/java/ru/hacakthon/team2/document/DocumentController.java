package ru.hacakthon.team2.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/document")
@CrossOrigin
public class DocumentController {

    @Autowired
    DocumentDao documentDao;

    @Autowired
    DoctypeDao doctypeDao;

    @GetMapping
    public ResponseEntity getDocumentsByDoctype(@RequestParam(required = true) Long doctypeId) throws Exception {
        if(doctypeDao.getById(doctypeId) == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No doctype with id " + doctypeId);

        List<Document> documentList = documentDao.getAllByDoctype(doctypeId);

        JSONArray documentArray = new JSONArray();

        for(Document document : documentList) {
            documentArray.add(documentDao.documentToJson(document));
        }
        return ResponseEntity.status(HttpStatus.OK).body(documentArray.toJSONString());
    }

    @GetMapping("/{id}")
    public ResponseEntity getDocumentById(@PathVariable Long id) throws Exception {
        Document document = documentDao.getById(id);

        if(document == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No document with id " + id);

        return ResponseEntity.status(HttpStatus.OK).body(documentDao.documentToJson(document).toJSONString());
    }

    @PostMapping("/{id}")
    public ResponseEntity updateDocument(@PathVariable Long id, @RequestBody String jsonDocumentString) throws Exception {
        System.out.println(jsonDocumentString);

        JSONObject jsonDocument = (JSONObject) JSONValue.parse(jsonDocumentString);

        Document documentToUpdate = documentDao.getById(id);

        if(documentToUpdate == null) return ResponseEntity.status(HttpStatus.OK).body("No document with id " + id + " found");

        Doctype doctype = doctypeDao.getById(documentToUpdate.getDoctypeId());

        documentToUpdate.setChecked(Boolean.valueOf(jsonDocument.get("checked").toString()));
        documentToUpdate.setOriginal(jsonDocument.get("original").toString().replace(doctype.getOriginalLocation(), ""));

        List<String> fieldNames = doctype.getFields();

        List<String> newFieldValues = new ArrayList<>();

        for(String fieldName : fieldNames) {
            String fieldValue = jsonDocument.get(fieldName).toString();

            if(fieldValue == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No field " + fieldName + " found");

            newFieldValues.add(fieldValue);
        }
        documentToUpdate.setFieldsValues(newFieldValues);

        boolean updated = documentDao.update(documentToUpdate);

        if(updated) return ResponseEntity.status(HttpStatus.OK).body(documentDao.documentToJson(documentDao.getById(id)).toJSONString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update failed");
    }
}
