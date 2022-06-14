package com.atypon.read_controller.models.layer_communication;

public enum MessageStatus {
    GOOD("GOOD"),               // resembles successful operations on the database.
    USER_ERROR("USER_ERROR"),   // resembles bad requests to the database.
    BAD("BAD");                 // resembles errors that happen while operating on the database.

    private final String status;

    MessageStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
