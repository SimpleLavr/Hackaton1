package ru.hacakthon.team2.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;
import ru.hacakthon.team2.document.DocumentDao;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/upload")
@CrossOrigin
public class FileController {

    @Autowired
    private IStorageService storageService;

    @Autowired
    private CsvParser csvParser;

    @Autowired
    private DoctypeDao doctypeDao;

    @Autowired
    private DocumentDao documentDao;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file, @RequestParam Long doctypeId) throws Exception {

        Doctype doctype = doctypeDao.getById(doctypeId);

        if(doctype == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No doctype with id " + doctypeId);

        Path savedFile = null;
        if(!file.getResource().getFilename().endsWith(".csv")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is not csv table");
        try {
            savedFile = storageService.store(file);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        List<ParsedDocument> parsedDocuments = csvParser.parseCsv(savedFile, doctype);

        for(ParsedDocument parsedDocument : parsedDocuments) {
            documentDao.create(parsedDocument.getFieldValues(), doctypeId, parsedDocument.getOriginal());
        }

        savedFile.toFile().delete();

        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }
}
