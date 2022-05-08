package ru.hacakthon.team2.document;

import org.springframework.beans.factory.annotation.Autowired;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;

import java.nio.file.Path;

public class DocumentLoader {

    @Autowired
    private DoctypeDao doctypeDao;

    public void loadDocument(Long doctypeId, Path csvFile) throws Exception {
        Doctype doctype = doctypeDao.getById(doctypeId);
        if(doctype == null) throw new Exception("No doctype with id " + doctypeId);

    }
}
