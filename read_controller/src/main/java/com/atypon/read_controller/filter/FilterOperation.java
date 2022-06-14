package com.atypon.read_controller.filter;

public enum FilterOperation {
    EQUALS("equals") {
        @Override
        boolean compare(Object value1, Object value2) {
            return value1.equals(value2);
        }
    },
    NOT_EQUALS("notEquals"){
        @Override
        boolean compare(Object value1, Object value2) {
            return !value1.equals(value2);
        }
    };

    private final String value;

    FilterOperation(String value) {
        this.value = value;
    }

    public static FilterOperation fromString(String value) {
        if(value.equals("equals")) return EQUALS;
        else if(value.equals("notEquals")) return NOT_EQUALS;
        return null;
    }

    abstract boolean compare(Object value1, Object value2);

    @Override
    public String toString() {
        return value;
    }
}
