package ru.hacakthon.team2.doctype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctype")
@CrossOrigin
public class DoctypeController {

    @Autowired
    private DoctypeDao doctypeDao;

    @GetMapping
    public List<Doctype> getAllDoctypes() {
        return doctypeDao.getAll();
    }

    @GetMapping("/{id}")
    public Doctype getById(@PathVariable Long id) {
        return doctypeDao.getById(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody Doctype doctype) {
        if(!doctype.getOriginalLocation().endsWith("/")) doctype.setOriginalLocation(doctype.getOriginalLocation() + "/");

        try {
            doctypeDao.create(doctype);
        } catch(DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request failed: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Doctype " + doctype.getName() + " successfully created");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {

        boolean deleted;

        try {
            deleted = doctypeDao.deleteById(id);
        } catch(DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request failed: " + e.getMessage());
        }
        if(deleted)
            return ResponseEntity.status(HttpStatus.OK).body("Doctype with id " + id + " successfully deleted");
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No doctype with id " + id);
    }


}
