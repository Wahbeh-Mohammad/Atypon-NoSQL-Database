package com.atypon.admin_controller.operations;

import com.atypon.admin_controller.json_io.JSONDeleter;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import com.atypon.admin_controller.utils.PathBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DeleteOperations {
    private final JSONDeleter deleter;

    @Autowired
    public DeleteOperations(JSONDeleter deleter) {
        this.deleter = deleter;
    }

    public AbstractMessage deleteSchema(String databaseName, String schemaName) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR, "Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist");

        String  pathToSchema       = PathBuilder.buildPathToSchema(databaseName, schemaName),
                pathToAllDocuments = PathBuilder.buildPathToAllDocuments(databaseName, schemaName);

        synchronized (this) {
            return deleter.deleteSchema(pathToSchema, pathToAllDocuments);
        }
    }

    public AbstractMessage deleteDocument(String databaseName, String schemaName, int id) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR, "Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist");

        String pathToDocument = PathBuilder.buildPathToDocument(databaseName, schemaName, id);

        synchronized (this) {
            return deleter.deleteDocument(pathToDocument);
        }
    }

    public AbstractMessage deleteAllDocuments(String databaseName, String schemaName) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR, "Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist");

        String pathToDocuments = PathBuilder.buildPathToAllDocuments(databaseName, schemaName);

        synchronized (this) {
            return deleter.deleteAllDocuments(pathToDocuments);
        }
    }

    private boolean databaseDoesNotExist(String databaseName) {
        File databaseFile = new File(PathBuilder.buildPathToDatabase(databaseName));
        return !databaseFile.exists();
    }

    private boolean schemaDoesNotExist(String databaseName, String schemaName) {
        File schemaFile = new File(PathBuilder.buildPathToSchema(databaseName, schemaName));
        return !schemaFile.exists();
    }
}
