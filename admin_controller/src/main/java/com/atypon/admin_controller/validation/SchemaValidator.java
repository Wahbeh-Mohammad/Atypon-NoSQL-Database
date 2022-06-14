package com.atypon.admin_controller.validation;

import com.atypon.admin_controller.flow.InvalidSchemaException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;


public class SchemaValidator {
    public static void isValid(String schemaName, JSONObject schema) throws InvalidSchemaException {
        if(schemaName == null)
            throw new InvalidSchemaException("Schema's \"schemaName\" field must not be null.");

        if(schema == null || schema.isEmpty())
            throw new InvalidSchemaException("Schema's \"schema\" field must not be null or empty.");

        // Validate field names, and types.
        // 1. field names must not be empty.
        // 2. types should be one of these [String, Integer, Array, Boolean]
        Set<String> schemaFields = schema.keySet();

        for(String fieldName: schemaFields) {
            String fieldType;

            try {
                fieldType = schema.getString(fieldName);
            } catch(JSONException e) {
                throw new InvalidSchemaException(e.getMessage());
            }

            if(isNullOrEmpty(fieldName))
                throw new InvalidSchemaException("Field name cannot be null or empty { "+ fieldName + " }");
            if(isNullOrEmpty(fieldType))
                throw new InvalidSchemaException("Field Type cannot be null or empty { " + fieldType + " }");
            if(!isValidFieldType(fieldType))
                throw new InvalidSchemaException("Invalid Field Type { " + fieldType + " }");
        }
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private static boolean isValidFieldType(String fieldType) {
        return fieldType.equals("String") || fieldType.equals("Array") ||
               fieldType.equals("Integer") || fieldType.equals("Boolean");
    }
}
