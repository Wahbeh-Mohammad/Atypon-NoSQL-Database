package com.atypon.admin_controller.models.types;

public enum UserRole {
    ADMIN("ADMIN"),
    REGULAR("REGULAR");

    private final String type;

    UserRole(String type) {
        this.type = type;
    }

    public static UserRole fromString(String type) {
        if (type.equals("ADMIN")) {
            return ADMIN;
        }
        return REGULAR;
    }

    @Override
    public String toString() {
        return type;
    }
}
