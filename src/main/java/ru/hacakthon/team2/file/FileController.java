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

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file, @PathVariable Long doctypeId) throws Exception {
        Path savedFile = null;
        if(!file.getResource().getFilename().endsWith(".csv")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is not csv table");
        try {
            savedFile = storageService.store(file);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }
}
