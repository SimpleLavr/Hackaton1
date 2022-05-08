package ru.hacakthon.team2.document;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hacakthon.team2.doctype.Doctype;
import ru.hacakthon.team2.doctype.DoctypeDao;

import java.util.List;

public class DocumentUtils {

    @Autowired
    private DoctypeDao doctypeDao;

    public JSONObject documentToJson(Document document) throws Exception {
//        if(doctype.getId() != document.getDoctypeId()) throw new Exception("Document has other doctype");
//        StringBuilder json = new StringBuilder();
//        List<String> namesList = doctype.getFields();
//        List<String> valueList = document.getFieldsValues();
//
//        json.append("{\"id\":\"").append(document.getId()).append("\",\"original\":\"")
//                .append(doctype.getOriginalLocationUrl()).append(document.getOriginal()).append("\",\"checked\":")
//                .append(document.isChecked());
//
//        for(int i = 0; i < valueList.size(); i++) {
//            json.append(",\"").append(namesList.get(i)).append("\":")
//                    .append("\"").append(valueList.get(i)).append("\"");
//        }
//        json.append("}");
//
//        return json.toString();
        JSONObject jsonDocument = new JSONObject();

        Doctype doctype = doctypeDao.getById(document.getDoctypeId());

        if(doctype == null) throw new Exception("No doctype with id " + document.getDoctypeId());

        List<String> namesList = doctype.getFields();
        List<String> valueList = document.getFieldsValues();

        jsonDocument.put("id",document.getId());
        jsonDocument.put("original",doctype.getOriginalLocation() + document.getOriginal());
        jsonDocument.put("checked",document.isChecked());

        for(int i = 0; i < namesList.size(); i++) {
            jsonDocument.put(namesList.get(i),valueList.get(i));
        }
        return jsonDocument;
    }
}
