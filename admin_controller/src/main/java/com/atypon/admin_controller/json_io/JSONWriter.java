package com.atypon.admin_controller.json_io;

import com.atypon.admin_controller.io.write.FileWriter;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;

@Component
public class JSONWriter {
    public AbstractMessage writeDocument(String pathToNewDocument, JSONObject document) {
        try {
            FileWriter fileWriter = new FileWriter(new File(pathToNewDocument), document.toString(2));
            fileWriter.writeFile();
            return new Message(MessageStatus.GOOD, "New document created");
        } catch (FileAlreadyExistsException e) {
            // writing a document is indexed internally i.e. the path and file name is built using the schema's information,
            // if the server tries to write a document and there is an id conflict, then this means
            // that something in the schema's information was altered in a bad way
            // or a file with the specific document id is added to the schema-docs manually.
            return new Message(MessageStatus.BAD, "Id Conflict, The schema's info was edited manually or a document was added manually not through server.");
        }
    }

    public AbstractMessage writeSchema(String pathToNewSchema, JSONObject schema) {
        try {
            FileWriter fileWriter = new FileWriter(new File(pathToNewSchema), schema.toString(2));
            fileWriter.writeFile();
            return new Message(MessageStatus.GOOD, "New schema created");
        } catch (FileAlreadyExistsException e) {
            return new Message(MessageStatus.USER_ERROR, "Schema already exists");
        }
    }
}
