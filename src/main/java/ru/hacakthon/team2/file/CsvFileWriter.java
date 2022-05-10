package ru.hacakthon.team2.file;

import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.QuoteStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.hacakthon.team2.changes.ChangesDao;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.document.Document;
import ru.hacakthon.team2.document.DocumentDao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

@Component
public class CsvFileWriter {

    @Autowired
    private Environment environment;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private ChangesDao changesDao;

    private final Charset WINDOWS_1251 = Charset.forName("windows-1251");

    public void writeCsvFile(Doctype doctype, Path fileToWrite) throws Exception {
        CsvWriter csvWriter = CsvWriter.builder()
                .fieldSeparator(';')
                .quoteCharacter('"')
                .quoteStrategy(QuoteStrategy.REQUIRED)
                .build(fileToWrite, WINDOWS_1251);

        List<Document> documentList = documentDao.getAllByDoctype(doctype.getId());

        List<String> headerRow = doctype.getFields();
        headerRow.add("original file");

        csvWriter.writeRow(headerRow);

        for(Document document : documentList) {
            List<String> fieldsValues = changesDao.getValuesListByDocumentId(document.getId());
            if(fieldsValues == null) fieldsValues = document.getFieldsValues();

            fieldsValues.add(document.getOriginal());
            csvWriter.writeRow(fieldsValues);
        }
        csvWriter.close();
    }
}
