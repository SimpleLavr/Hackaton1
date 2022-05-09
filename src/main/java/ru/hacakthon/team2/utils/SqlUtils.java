package ru.hacakthon.team2.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hacakthon.team2.document.Document;

import java.io.*;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class SqlUtils {

    private static Logger logger = LoggerFactory.getLogger(SqlUtils.class);

    public static String toSqlArray(List<String> list) {
        String array = "\'{";

        for (String value : list) {
            array += ",\"" + StringEscapeUtils.escapeSql(value).replaceAll("\"", "\\\\\"") + "\"";
        }
        array = array.replaceFirst(",", "") + "}\'";
        return array;
    }

    public static Long getChecksum(Serializable object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ObjectOutputStream oos;

        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);

            oos.flush();
            oos.close();
        } catch(IOException e) {
            logger.warn("Failed to get checksum of {}: {}", object.getClass(), e.getMessage());
            throw e;
        }

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        CheckedInputStream checkedInputStream = new CheckedInputStream(is, new CRC32());
        byte[] buffer = new byte[1024];
        while (checkedInputStream.read(buffer, 0, buffer.length) >= 0) {}
        return checkedInputStream.getChecksum().getValue();
    }
}
