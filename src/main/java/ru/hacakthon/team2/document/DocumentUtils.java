package ru.hacakthon.team2.document;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;
import ru.hacakthon.team2.utils.SqlUtils;

import java.io.*;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

@Component
public class DocumentUtils {

    private static Logger logger = LoggerFactory.getLogger(DocumentUtils.class);


    public static JSONObject documentToJson(Document document, Doctype doctype) throws Exception {

        JSONObject jsonDocument = new JSONObject();

        List<String> namesList = doctype.getFields();
        List<String> valueList = document.getFieldsValues();

        jsonDocument.put("id",document.getId());
        jsonDocument.put("original",doctype.getOriginalLocation() + document.getOriginal());
        jsonDocument.put("checked",document.isChecked());
        jsonDocument.put("checksum", SqlUtils.getChecksum(document));
        jsonDocument.put("changed", document.isChanged());

        for(int i = 0; i < namesList.size(); i++) {
            jsonDocument.put(namesList.get(i),valueList.get(i));
        }
        return jsonDocument;
    }


    public static Long getChecksum(Document document) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ObjectOutputStream oos;

        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(document);

            oos.flush();
            oos.close();
        } catch(IOException e) {
            logger.warn("Failed to get checksum of document with id {}: {}", document.getId(), e.getMessage());
            throw e;
        }

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        CheckedInputStream checkedInputStream = new CheckedInputStream(is, new CRC32());
        byte[] buffer = new byte[1024];
        while (checkedInputStream.read(buffer, 0, buffer.length) >= 0) {}
        return checkedInputStream.getChecksum().getValue();
    }
}
