package ru.hacakthon.team2.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/upload")
public class FileController {

    @Autowired
    private IStorageService storageService;

    @Autowired
    private ZipArchiver zipArchiver;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file) throws Exception {
        Path savedFile = null;
        if(!file.getResource().getFilename().endsWith(".zip")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is not zip archive");
        try {
            savedFile = storageService.store(file);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        zipArchiver.unpack(savedFile);
        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }
}
