package com.atypon.admin_controller.json_io;

import com.atypon.admin_controller.io.delete.FileDeleter;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class JSONDeleter {
    public AbstractMessage deleteDocument(String pathToDocument) {
        try {
            FileDeleter fileDeleter = new FileDeleter(new File(pathToDocument));
            fileDeleter.deleteFile();
            return new Message(MessageStatus.GOOD, "Document deleted successfully");
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "Couldn't find document to delete.");
        }
    }

    public AbstractMessage deleteAllDocuments(String pathToAllDocuments) {
        try {
            File allDocumentsFile = new File(pathToAllDocuments);
            if(allDocumentsFile.listFiles() == null || allDocumentsFile.listFiles().length == 0)
                throw new FileNotFoundException();

            FileDeleter fileDeleter = new FileDeleter(allDocumentsFile);
            fileDeleter.deleteFile();
            allDocumentsFile.mkdir();
            return new Message(MessageStatus.GOOD, "All documents are deleted");
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "There are no documents that relate to the schema.");
        }
    }

    public AbstractMessage deleteSchema(String pathToSchema, String pathToAllDocuments) {
        try {
            FileDeleter schemaDeleter = new FileDeleter(new File(pathToSchema));
            schemaDeleter.deleteFile();
            FileDeleter allDocumentsDeleter = new FileDeleter(new File(pathToAllDocuments));
            allDocumentsDeleter.deleteFile();
            return new Message(MessageStatus.GOOD, "Schema deleted");
        } catch ( FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, e.getMessage());
        }
    }
}
