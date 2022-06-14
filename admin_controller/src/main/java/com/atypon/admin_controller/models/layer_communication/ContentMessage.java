package com.atypon.admin_controller.models.layer_communication;

import org.json.JSONObject;

public class ContentMessage<ContentType> extends Message {
    private final ContentType content;

    public ContentMessage(MessageStatus status, String message, ContentType content){
        super(status, message);
        this.content = content;
    }

    public ContentType getContent() {
        return content;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonMessage = super.toJSON();
        jsonMessage.put("content", this.content);
        return jsonMessage;
    }

    @Override
    public String toString() {
        return this.toJSON().toString(2);
    }
}
