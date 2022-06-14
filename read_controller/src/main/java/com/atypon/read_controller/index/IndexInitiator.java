package com.atypon.read_controller.index;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class IndexInitiator {
    public static JSONObject createIndex(JSONArray listOfData, String fieldName) {
        HashMap<String, JSONArray> indexedValues = new HashMap<>();
        for (int documentIndex = 0; documentIndex < listOfData.length(); documentIndex++) {
            JSONObject document = listOfData.getJSONObject(documentIndex);
            String fieldValue;

            if(document.isNull(fieldName)) fieldValue = "null";
            else fieldValue = document.get(fieldName).toString();

            if (!indexedValues.containsKey(fieldValue))
                indexedValues.put(fieldValue, new JSONArray());
            indexedValues.get(fieldValue).put(document);
        }
        return new JSONObject(indexedValues);
    }
}
