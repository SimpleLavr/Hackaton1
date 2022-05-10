package ru.hacakthon.team2.file;


import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hacakthon.team2.changes.ChangesDao;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.document.Document;
import ru.hacakthon.team2.document.DocumentDao;
import ru.hacakthon.team2.utils.SqlUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CsvParser {

    private final String ORIGINAL_HEADER = "файл";

    private final Logger logger = LoggerFactory.getLogger(CsvParser.class);

    @Autowired
    private DocumentDao documentDao;

    public List<ParsedDocument> parseCsv(Path csvFile, Doctype doctype, boolean updateDuplicates) throws Exception {

        List<CsvRow> rows = CsvReader.builder()
                .fieldSeparator(';')
                .quoteCharacter('"')
                .build(csvFile, Charset.forName("windows-1251"))
                .stream().toList();

        int original = -1;

        CsvRow headerRow = rows.get(0);
        Map<String, Integer> headers = new HashMap<>();
        for(int i = 0; i < headerRow.getFieldCount(); i++) {
            headers.put(headerRow.getField(i), i);
            if(headerRow.getField(i).toLowerCase(Locale.ROOT).equals(ORIGINAL_HEADER)) original = i;
        }

        if(original < 0) throw new IOException("No original file field");

//        HashMap<Long, Long> checksumIdMap = getChecksumMap(doctype);

        List<ParsedDocument> parsedDocuments = new ArrayList<>();

        for(int i = 1; i < rows.size(); i++) {
            CsvRow currentRow = rows.get(i);
            if(currentRow.getFieldCount() < headerRow.getFieldCount()) {
                logger.debug("Error in line " + (i + 1) + ": line has less columns than header. Skipping");
                continue;
            }

            String originalFile = currentRow.getField(original);

            if(!originalFile.endsWith(".pdf")) {
                logger.debug("Error in line " + (i + 1) + ": line does not have proper original file: " + originalFile + ". Skipping");
            }
            List<String> fieldsValues = new ArrayList<>();

            for(String fieldName : doctype.getFields()) {
                fieldsValues.add(currentRow.getField(headers.get(fieldName)));
            }

            //TODO доделать таки версию с чексуммами
            ParsedDocument parsedDocument = new ParsedDocument(originalFile, fieldsValues);

            Long existingDocumentId = documentDao.getDocumentByValues(fieldsValues);
            if(existingDocumentId != null) {
                logger.debug("Line {} is a duplicate of a document with id {}...", i + 1, existingDocumentId);
                if(updateDuplicates) {
                    logger.debug("...update_duplicates == true, updating line {}", i + 1);
                    documentDao.delete(existingDocumentId);
                }
                else {
                    logger.debug("...update duplicates == false, skipping line {}", i + 1);
                    continue;
                }
            }

            parsedDocuments.add(parsedDocument);

        }


        return parsedDocuments;
    }


    //Returns hashMap of document <checksum, documentId>
    // Checksum, however, is calculated only based on original and fieldsNames(or changes.fieldsNames, if document was updated)
    public HashMap<Long, Long> getChecksumMap(Doctype doctype) throws Exception {

        List<Document> documentList = documentDao.getAllByDoctype(doctype.getId());

        HashMap<Long, Long> hashMap = new HashMap<>();

        for(Document document : documentList) {
            ParsedDocument parsedDocument = new ParsedDocument(document.getOriginal(), document.getFieldsValues());
            hashMap.put(SqlUtils.getChecksum(parsedDocument), document.getId());
        }
        return hashMap;
    }
}
