package ru.hacakthon.team2.doctype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctype")
@CrossOrigin
public class DoctypeController {

    @Autowired
    DoctypeDao doctypeDao;

    @GetMapping
    public List<Doctype> getAllDoctypes() {
        return doctypeDao.getAll();
    }

    @GetMapping("/{id}")
    public Doctype getById(@PathVariable Long id) {
        return doctypeDao.getById(id);
    }

    @PostMapping
    public void create(@RequestBody Doctype doctype) {
        if(!doctype.getOriginalLocationUrl().endsWith("/")) doctype.setOriginalLocationUrl(doctype.getOriginalLocationUrl() + "/");
        doctypeDao.create(doctype);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = doctypeDao.deleteById(id);

        if(deleted) return ResponseEntity.status(HttpStatus.OK).body("Deleted");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No doctype with this id");
    }


}
