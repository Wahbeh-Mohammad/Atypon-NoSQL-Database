package com.atypon.read_controller.filter;

import org.json.JSONArray;
import org.json.JSONObject;

public class Filter<Type> {
    JSONArray data;
    FilterCriteria<Type> criteria;

    private Filter(JSONArray data, FilterCriteria<Type> criteria) {
        this.data = data;
        this.criteria = criteria;
    }

    public static Filter<?> createFilter(JSONArray allData, String fieldType, String fieldName, String operation, String compareTo) {
        try {
            switch (fieldType) {
                case "String":
                    return new Filter<>(allData, new FilterCriteria<>(fieldName, operation, compareTo));
                case "Integer":
                    return new Filter<>(allData, new FilterCriteria<>(fieldName, operation, Integer.parseInt(compareTo)));
                case "Boolean":
                    boolean compareToCasted = compareTo.equals("true");
                    return new Filter<>(allData, new FilterCriteria<>(fieldName, operation, compareToCasted));
                default:
                    return null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public JSONArray filter() {
        JSONArray filteredData = new JSONArray();

        String fieldName = criteria.getFieldName();
        FilterOperation operation = criteria.getOperation();
        Type compareTo = criteria.getCompareTo();

        for(int objectIdx = 0; objectIdx < data.length(); objectIdx++) {
            JSONObject curObject = data.getJSONObject(objectIdx);
            if(curObject.isNull(fieldName))
                continue;

            Type value = (Type) curObject.get(fieldName);
            if(operation.compare(value, compareTo)) {
                filteredData.put(curObject);
            }
        }
        return filteredData;
    }
}
