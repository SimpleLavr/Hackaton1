package ru.hacakthon.team2.doctype;

import java.util.ArrayList;
import java.util.List;

public class Doctype {

    private Long id;

    private List<String> fields;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
