package com.atypon.admin_controller.json_io;

import com.atypon.admin_controller.io.update.FileUpdater;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class JSONUpdater {
    public AbstractMessage updateDocument(String pathOfUpdate, String documentId, JSONObject updates) {
        try {
            FileUpdater fileUpdater = new FileUpdater(pathOfUpdate, String.valueOf(documentId), ".json", updates.toString(2));
            fileUpdater.updateFile();
            return new Message(MessageStatus.GOOD, "Document updated successfully");
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "Document not found");
        }
    }

    public AbstractMessage updateSchema(String pathOfUpdate, String schemaName, JSONObject updates) {
        try {
            FileUpdater fileUpdater = new FileUpdater(pathOfUpdate, schemaName, ".json", updates.toString(2));
            fileUpdater.updateFile();
            return new Message(MessageStatus.GOOD, "Schema updated successfully");
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "Schema not found");
        }
    }
}
