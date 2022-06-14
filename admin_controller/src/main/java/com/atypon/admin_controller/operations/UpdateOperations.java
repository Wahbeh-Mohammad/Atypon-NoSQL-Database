package com.atypon.admin_controller.operations;

import com.atypon.admin_controller.flow.InvalidDocumentException;
import com.atypon.admin_controller.flow.InvalidSchemaException;
import com.atypon.admin_controller.io.read.SynchronizedFileReader;
import com.atypon.admin_controller.json_io.JSONUpdater;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import com.atypon.admin_controller.utils.PathBuilder;
import com.atypon.admin_controller.validation.DocumentValidator;
import com.atypon.admin_controller.validation.SchemaValidator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class UpdateOperations {
    private final JSONUpdater updater;

    @Autowired
    public UpdateOperations(JSONUpdater updater) {
        this.updater = updater;
    }

    public AbstractMessage updateDocument(String databaseName, String schemaName, int id, JSONObject updatedDocument) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR, "Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist");

        try {
            synchronized (this) {
                JSONObject schema = fetchSchema(databaseName, schemaName).getJSONObject("schema");
                DocumentValidator.isValidDocument(schema, updatedDocument);

                String pathOfUpdate = PathBuilder.buildPathToAllDocuments(databaseName, schemaName);
                updatedDocument.put("_id", id);
                return updater.updateDocument(pathOfUpdate, String.valueOf(id), updatedDocument);
            }
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist");
        } catch (InvalidDocumentException e) {
            return new Message(MessageStatus.USER_ERROR, e.getMessage());
        }
    }

    public AbstractMessage updateSchema(String databaseName, String schemaName, JSONObject updates) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR, "Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist");

        try {
            synchronized (this) {
                SchemaValidator.isValid(schemaName, updates);

                JSONObject oldSchema  = fetchSchema(databaseName, schemaName),
                        oldSchemaInfo = oldSchema.getJSONObject("info"),
                        newFullSchema = new JSONObject();

                updates.put("_id", "Integer");
                newFullSchema.put("schema", updates);
                newFullSchema.put("info", oldSchemaInfo);

                String pathOfUpdates = PathBuilder.buildPathToAllSchemas(databaseName);
                return updater.updateSchema(pathOfUpdates, schemaName, newFullSchema);
            }
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist");
        } catch (InvalidSchemaException e) {
            return new Message(MessageStatus.USER_ERROR, e.getMessage());
        }
    }

    private JSONObject fetchSchema(String databaseName, String schemaName) throws FileNotFoundException {
        String pathToSchema = PathBuilder.buildPathToSchema(databaseName, schemaName);
        SynchronizedFileReader reader = new SynchronizedFileReader(new File(pathToSchema));
        reader.readFile();
        return new JSONObject(reader.getContent());
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
