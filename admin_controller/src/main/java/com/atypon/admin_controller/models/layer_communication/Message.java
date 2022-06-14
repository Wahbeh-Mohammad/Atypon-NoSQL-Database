package com.atypon.admin_controller.models.layer_communication;

import org.json.JSONObject;

public class Message implements AbstractMessage {
    private final MessageStatus status;
    private final String message;

    public Message(MessageStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public MessageStatus getStatus() {
        return status;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("message", message);
        jsonMessage.put("status", status.toString());
        return jsonMessage;
    }

    @Override
    public boolean isGood() {
        return this.status.equals(MessageStatus.GOOD);
    }

    @Override
    public String toString() {
        return this.toJSON().toString(2);
    }

}
