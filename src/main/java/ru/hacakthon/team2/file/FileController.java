package ru.hacakthon.team2.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;
import ru.hacakthon.team2.document.DocumentDao;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@CrossOrigin
public class FileController {

    @Autowired
    private IStorageService storageService;

    @Autowired
    private CsvParser csvParser;

    @Autowired
    private CsvFileWriter csvFileWriter;

    @Autowired
    private DoctypeDao doctypeDao;

    @Autowired
    private DocumentDao documentDao;

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestBody MultipartFile file, @RequestParam Long doctypeId,
                                        @RequestParam(required = false) boolean updateDuplicates) throws Exception {

        Doctype doctype = doctypeDao.getById(doctypeId);

        if(doctype == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No doctype with id " + doctypeId);

        Path savedFile = null;
        if(!file.getResource().getFilename().endsWith(".csv")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is not csv table");
        try {
            savedFile = storageService.store(file);
        } catch (Exception e) {
            e.printStackTrace();
            if(savedFile != null) savedFile.toFile().delete();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        List<ParsedDocument> parsedDocuments = csvParser.readCsv(savedFile, doctype, false);

        for(ParsedDocument parsedDocument : parsedDocuments) {
            documentDao.create(parsedDocument.getFieldValues(), doctypeId, parsedDocument.getOriginal());
        }

        savedFile.toFile().delete();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body("File successfully uploaded");
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam Long doctypeId) throws Exception {

        Doctype doctype = doctypeDao.getById(doctypeId);
        if(doctype == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No doctype with id " + doctypeId);

        Path fileToWrite = storageService.load(doctype.getName() + "_fileForDownload.csv");

        csvFileWriter.writeCsvFile(doctype, fileToWrite, true);

        Resource fileToSend = new ByteArrayResource(Files.readAllBytes(fileToWrite));

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.asMediaType(MimeType.valueOf("text/csv")))
                    .body(fileToSend);
        } finally {
            fileToWrite.toFile().delete();
        }
    }
}
