package com.atypon.admin_controller.validation;

import com.atypon.admin_controller.flow.InvalidDocumentException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class DocumentValidator {
    public static synchronized void isValidDocument(JSONObject schema, JSONObject requestDocument ) throws InvalidDocumentException {
        Set<String> fieldNames = schema.keySet();
        Set<String> documentFields = requestDocument.keySet();

        if(documentFields.size() > fieldNames.size())
            throw new InvalidDocumentException("Document contains extra fields.");

        for(String fieldName : fieldNames) {
            String fieldType = schema.getString(fieldName);
            if(fieldName.equals("_id")) continue;

            // check if document doesn't have the key
            if(requestDocument.isNull(fieldName))
                throw new InvalidDocumentException("Document is invalid must contain key: { "+ fieldName +" }");

            try {
                // we can check data types of the fields sent using get methods of
                // json object if something is wrong it will throw a json exception
                switch (fieldType) {
                    case "String":
                        requestDocument.getString(fieldName);
                        break;
                    case "Array":
                        requestDocument.getJSONArray(fieldName);
                        break;
                    case "Integer":
                        requestDocument.getInt(fieldName);
                        break;
                    case "Boolean":
                        requestDocument.getBoolean(fieldName);
                        break;
                }
            } catch(JSONException e) {
                throw new InvalidDocumentException("Field { " + fieldName + " } has invalid value: { "+ requestDocument.get(fieldName) +" }");
            }
        }
    }
}
