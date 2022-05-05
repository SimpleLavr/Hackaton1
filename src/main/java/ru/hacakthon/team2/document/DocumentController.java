package ru.hacakthon.team2.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public String getDocumentsByDoctype(@PathVariable(required = true) Long doctype) throws Exception {
        return documentDao.getAllByDoctypeInJson(doctype);
    }

    @GetMapping("/{id}")
    public String getDocumentById(@PathVariable Long id) {
        return documentDao.getJsonById(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestBody String jsonDocument) throws Exception {

        Map<String,Object> mapDocument = new ObjectMapper().readValue(jsonDocument, Map.class);

        Document documentToUpdate = documentDao.getById(id);

        if(documentToUpdate == null) throw new Exception("No document with id " + id);

        Doctype doctype = doctypeDao.getById(documentToUpdate.getDoctypeId());

        documentToUpdate.setChecked((boolean) mapDocument.get("checked"));
        documentToUpdate.setOriginal(mapDocument.get("original").toString());

        List<String> fieldsValues = new ArrayList<>();

        for(String fieldName : doctype.getFields()) {
            String fieldValue = mapDocument.get(fieldName).toString();

            if(fieldValue == null) throw new Exception("No field: " + fieldName);
        }
        documentToUpdate.setFieldsValues(fieldsValues);

        boolean updated = documentDao.update(documentToUpdate);

        if(updated) return ResponseEntity.status(HttpStatus.OK).body(documentDao.getJsonById(id));
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Document was not updated");
    }
}
