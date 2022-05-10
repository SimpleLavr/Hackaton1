package ru.hacakthon.team2.document;

import java.io.Serializable;
import java.util.List;

public class Document implements Serializable {

    private Long id;

    private Long doctypeId;

    private List<String> fieldsValues;

    private String original;

    private boolean checked;

    private boolean changed;

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Long getId() {
        return id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getFieldsValues() {
        return fieldsValues;
    }

    public Long getDoctypeId() {
        return doctypeId;
    }

    public void setDoctypeId(Long doctypeId) {
        this.doctypeId = doctypeId;
    }

    public void setFieldsValues(List<String> fieldsValues) {
        this.fieldsValues = fieldsValues;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
