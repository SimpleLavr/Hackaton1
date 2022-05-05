package ru.hacakthon.team2.file;

import java.util.List;

public class ParsedDocument {

    private List<String> fieldValues;
    private String original;

    public ParsedDocument(String original, List<String> fieldValues) {
        this.original = original;
        this.fieldValues = fieldValues;
    }

    public List<String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
