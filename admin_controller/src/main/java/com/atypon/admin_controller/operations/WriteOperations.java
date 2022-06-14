package com.atypon.admin_controller.operations;

import com.atypon.admin_controller.flow.AlreadyExistsException;
import com.atypon.admin_controller.flow.InvalidDocumentException;
import com.atypon.admin_controller.flow.InvalidSchemaException;
import com.atypon.admin_controller.io.directory.DirectoryCreator;
import com.atypon.admin_controller.io.read.SynchronizedFileReader;
import com.atypon.admin_controller.io.update.FileUpdater;
import com.atypon.admin_controller.json_io.JSONWriter;
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
import java.util.HashMap;

@Component
public class WriteOperations {
    private final JSONWriter writer;

    @Autowired
    public WriteOperations(JSONWriter writer) {
        this.writer = writer;
    }

    public AbstractMessage createNewDocument(String databaseName, String schemaName, JSONObject newDocument) {
        if(databaseDoesNotExist(databaseName))
            return new Message( MessageStatus.USER_ERROR, "Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message( MessageStatus.USER_ERROR, "Schema doesn't exist");

        try {
            String pathToSchema      = PathBuilder.buildPathToSchema(databaseName, schemaName),
                   pathToSchemasRoot = PathBuilder.buildPathToAllSchemas(databaseName);

            synchronized (this) {
                JSONObject fullSchema = fetchSchema(new File(pathToSchema)),
                           schema     = fullSchema.getJSONObject("schema"),
                           schemaInfo = fullSchema.getJSONObject("info");

                DocumentValidator.isValidDocument(schema, newDocument);
                int nextId = schemaInfo.getInt("next_id");
                newDocument.put("_id", nextId);

                String pathToNewDocument = PathBuilder.buildPathToDocument(databaseName, schemaName, nextId);
                AbstractMessage writerMessage = writer.writeDocument(pathToNewDocument, newDocument);
                // if writing was done correctly, update the next_id prop in the schema
                if (writerMessage.isGood()) {
                    schemaInfo.put("next_id", nextId + 1);
                    fullSchema.put("info", schemaInfo);
                    updateSchema(pathToSchemasRoot, schemaName, fullSchema);
                }
                return writerMessage;
            }
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "Schema { " + schemaName + " }, doesn't exist");
        } catch (InvalidDocumentException e) {
            return new Message(MessageStatus.USER_ERROR, e.getMessage());
        }
    }

    public AbstractMessage createNewSchema(String databaseName, String schemaName, JSONObject schema) {
        if(databaseDoesNotExist(databaseName))
            return new Message( MessageStatus.USER_ERROR, "Database doesn't exist");

        try {
            // throws InvalidSchemaException with a descriptive message
            SchemaValidator.isValid(schemaName, schema);
            synchronized (this) {
                // 1. add the _id field to the schema.
                // 2. set up new schema information
                // 3. dispatch full schema to the json writer.
                JSONObject  fullSchema = new JSONObject(),
                            info       = new JSONObject();

                schema.put("_id", "Integer");
                info.put("next_id", 1);
                info.put("schema_name", schemaName);
                fullSchema.put("schema", schema);
                fullSchema.put("info", info);

                System.out.println(fullSchema);

                String pathToNewSchema          = PathBuilder.buildPathToSchema(databaseName, schemaName),
                       pathToNewSchemaDocuments = PathBuilder.buildPathToAllDocuments(databaseName, schemaName);

                AbstractMessage writerMessage = writer.writeSchema(pathToNewSchema, fullSchema);
                if (writerMessage.getStatus().equals(MessageStatus.GOOD)) {
                    // this resembles a new schema creation, create a directory where the documents will lie in.
                    DirectoryCreator directoryCreator = new DirectoryCreator(new File(pathToNewSchemaDocuments));
                    directoryCreator.createDirectory();
                }
                return writerMessage;
            }
        } catch (InvalidSchemaException e) {
            return new Message(MessageStatus.USER_ERROR, e.getMessage());
        } catch (AlreadyExistsException e) {
            return new Message(MessageStatus.BAD, e.getMessage());
        }
    }

    private JSONObject fetchSchema(File schemaFile) throws FileNotFoundException {
        SynchronizedFileReader reader = new SynchronizedFileReader(schemaFile);
        reader.readFile();
        return new JSONObject(reader.getContent());
    }

    private synchronized void updateSchema(String pathToSchemasRoot, String schemaName, JSONObject updatedSchema) throws FileNotFoundException {
        FileUpdater fileUpdater = new FileUpdater(pathToSchemasRoot, schemaName, ".json", updatedSchema.toString(2));
        fileUpdater.updateFile();
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
