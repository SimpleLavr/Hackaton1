package ru.hacakthon.team2.utils;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

public class SqlUtils {

    public static String toSqlArray(List<String> list) {
        String array = "\'{";

        for (String value : list) {
            array += ",\"" + StringEscapeUtils.escapeSql(value).replaceAll("\"", "\\\\\"") + "\"";
        }
        array = array.replaceFirst(",", "") + "}\'";
        return array;
    }
}
