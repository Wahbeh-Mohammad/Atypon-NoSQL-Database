package com.atypon.read_controller.models.layer_communication;

import org.json.JSONObject;

public interface AbstractMessage {
    String getMessage();
    MessageStatus getStatus();
    JSONObject toJSON();
    String toString();
    boolean isGood();
}
