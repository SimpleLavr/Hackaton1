package ru.hacakthon.team2.utils;

import java.util.List;

public class SqlUtils {

    public static String toSqlArray(List<String> list) {
        String array = "\'{";

        for (String value : list) {
            array += ",\"" + value + "\"";
        }
        array = array.replaceFirst(",", "") + "}\'";
        return array;
    }
}
