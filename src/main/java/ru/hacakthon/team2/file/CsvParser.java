package ru.hacakthon.team2.file;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import ru.hacakthon.team2.doctype.Doctype;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

@Component
public class CsvParser {

    private final String ORIGINAL_HEADER = "файл";

    public List<ParsedDocument> parseCsv(Path csvFile, Doctype doctype) throws IOException {
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

        List<ParsedDocument> parsedDocuments = new ArrayList<>();

        for(int i = 1; i < records.size(); i++) {
            CSVRecord currentRecord = records.get(i);
            if(currentRecord.size() < headerRecord.size()) {
                //TODO add proper logging
                System.out.println("Error in line " + (i + 1) + ": line has less columns than header. Skipping");
                continue;
            }

            String originalFile = currentRecord.get(original);

            if(!originalFile.endsWith(".pdf")) {
                System.out.println("Error in line " + (i + 1) + ": line does not have proper original file: " + originalFile + ". Skipping");
            }
            List<String> fieldsValues = new ArrayList<>();

            for(String fieldName : doctype.getFields()) {
                fieldsValues.add(currentRecord.get(headers.get(fieldName)));
            }
            parsedDocuments.add(new ParsedDocument(originalFile, fieldsValues));

        }


        return parsedDocuments;
    }
}
