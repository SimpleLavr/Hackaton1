package ru.hacakthon.team2.file;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.document.Document;
import ru.hacakthon.team2.document.DocumentDao;
import ru.hacakthon.team2.utils.SqlUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

@Component
public class CsvParser {

    private final String ORIGINAL_HEADER = "файл";

    private final Logger logger = LoggerFactory.getLogger(CsvParser.class);

    @Autowired
    private DocumentDao documentDao;

    public List<ParsedDocument> parseCsv(Path csvFile, Doctype doctype, boolean updateDuplicates) throws Exception {
        Reader in = new FileReader(csvFile.toFile(), Charset.forName("windows-1251"));
        List<CSVRecord> records = CSVFormat.newFormat(';').parse(in).getRecords();

        int original = -1;

        CSVRecord headerRecord = records.get(0);
        Map<String, Integer> headers = new HashMap<>();
        for(int i = 0; i < headerRecord.size(); i++) {
            headers.put(headerRecord.get(i), i);
            if(headerRecord.get(i).toLowerCase(Locale.ROOT).equals(ORIGINAL_HEADER)) original = i;
        }
        if(original < 0) throw new IOException("No original file field");

        HashMap<Long, Long> checksumIdMap = getChecksumMap(doctype);

        List<ParsedDocument> parsedDocuments = new ArrayList<>();

        for(int i = 1; i < records.size(); i++) {
            CSVRecord currentRecord = records.get(i);
            if(currentRecord.size() < headerRecord.size()) {
                //TODO add proper logging
                logger.debug("Error in line " + (i + 1) + ": line has less columns than header. Skipping");
                continue;
            }

            String originalFile = currentRecord.get(original);

            if(!originalFile.endsWith(".pdf")) {
                logger.debug("Error in line " + (i + 1) + ": line does not have proper original file: " + originalFile + ". Skipping");
            }
            List<String> fieldsValues = new ArrayList<>();

            for(String fieldName : doctype.getFields()) {
                fieldsValues.add(currentRecord.get(headers.get(fieldName)));
            }

            ParsedDocument parsedDocument = new ParsedDocument(originalFile, fieldsValues);
            Long checksum = SqlUtils.getChecksum(parsedDocument);

            if(checksumIdMap.containsKey(checksum)) {
                logger.debug("Line {} is a duplicate of document with id {}...", i + 1, checksumIdMap.get(checksum));
                if(updateDuplicates) {
                    logger.debug("...update duplicates == true, updating line {}", i + 1);
                    documentDao.delete(checksumIdMap.get(checksum));
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


    //Returns hashMap of document <checksum, documentId>. Checksum, however, is calculated only based on original and fieldsNames
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
