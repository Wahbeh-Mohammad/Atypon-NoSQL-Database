package com.atypon.read_controller.filter;

public class FilterCriteria<Type> {
    private final String fieldName;
    private final Type compareTo;
    private final FilterOperation operation;

    public FilterCriteria(String fieldName, String operation, Type compareTo) {
        this.fieldName = fieldName;
        this.operation = FilterOperation.fromString(operation);
        if(this.operation == null)
            throw new IllegalArgumentException("Invalid comparison operation");
        this.compareTo = compareTo;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FilterOperation getOperation() {
        return operation;
    }

    public Type getCompareTo() {
        return compareTo;
    }
}
