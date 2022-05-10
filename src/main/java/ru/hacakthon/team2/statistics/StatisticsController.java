package ru.hacakthon.team2.statistics;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;
import ru.hacakthon.team2.document.DocumentDao;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    DoctypeDao doctypeDao;

    @Autowired
    DocumentDao documentDao;

    @GetMapping
    public ResponseEntity getStatistics(@RequestParam Long doctypeId) {

        Doctype doctype = doctypeDao.getById(doctypeId);
        if(doctype == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No doctype with id " + doctypeId + "found");

        JSONObject statistics = new JSONObject();

        statistics.put("uploaded", documentDao.getDocumentNumber(doctypeId));
        statistics.put("checked", documentDao.getCheckedDocumentsNumber(doctypeId));
        statistics.put("checkedWithErrors", documentDao.getCheckedAndChangedDocumentsNumber(doctypeId));

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(statistics.toJSONString());
    }
}
