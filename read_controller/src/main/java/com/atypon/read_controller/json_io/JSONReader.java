package com.atypon.read_controller.json_io;

import com.atypon.read_controller.io.read.AsynchronizedFileReader;
import com.atypon.read_controller.io.read.SynchronizedFileReader;
import com.atypon.read_controller.models.layer_communication.AbstractMessage;
import com.atypon.read_controller.models.layer_communication.ContentMessage;
import com.atypon.read_controller.models.layer_communication.Message;
import com.atypon.read_controller.models.layer_communication.MessageStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

@Component
public class JSONReader {

    public AbstractMessage readDocument(String pathToDocument) {
        try {
            SynchronizedFileReader reader = new SynchronizedFileReader(new File(pathToDocument));
            reader.readFile();
            return new ContentMessage<>(MessageStatus.GOOD, "Read successful", new JSONObject(reader.getContent()));
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "Document not found");
        }
    }

    public AbstractMessage readAllDocuments(String pathToAllDocuments) {
        try {
            JSONArray allDocuments = readMultipleFiles(pathToAllDocuments);
            return new ContentMessage<>(MessageStatus.GOOD, "Read successful", allDocuments);
        } catch (IllegalArgumentException e) {
            return new Message(MessageStatus.GOOD, "Schema has no documents");
        }
    }

    public AbstractMessage readSchema(String pathToSchema) {
        try {
            SynchronizedFileReader reader = new SynchronizedFileReader(new File(pathToSchema));
            reader.readFile();
            return new ContentMessage<>(MessageStatus.GOOD, "Read successful", new JSONObject(reader.getContent()));
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "No schema found with specified name");
        }
    }

    public AbstractMessage readAllSchemas(String pathToAllSchemas) {
        try {
            JSONArray allSchemas = readMultipleFiles(pathToAllSchemas);
            return new ContentMessage<>(MessageStatus.GOOD, "Read successful", allSchemas);
        } catch (IllegalArgumentException e) {
            return new Message(MessageStatus.GOOD, "Database has no schemas");
        }
    }

    private JSONArray readMultipleFiles(String pathToFiles) throws IllegalArgumentException {
        File file = new File(pathToFiles);
        File[] listOfFiles = file.listFiles();

        if(listOfFiles == null)
            throw new IllegalArgumentException();

        int numberOfFiles = listOfFiles.length, fileToReadIndex = 0;
        int coreCount = Runtime.getRuntime().availableProcessors();

        JSONArray allFiles = new JSONArray();
        ArrayList<AsynchronizedFileReader> readers = new ArrayList<>();

        while(fileToReadIndex < numberOfFiles) {
            ArrayList<Thread> readerThreads = new ArrayList<>();
            for(int threadIndex = 0; threadIndex < coreCount; threadIndex++) {
                if(fileToReadIndex == numberOfFiles)
                    break;

                AsynchronizedFileReader reader;
                try {
                    reader = new AsynchronizedFileReader(listOfFiles[fileToReadIndex]);
                } catch (FileNotFoundException e) {
                    fileToReadIndex++;
                    continue;
                }
                readers.add(reader);
                Thread readerThread = new Thread(reader);
                readerThreads.add(readerThread);
                readerThread.start();
                fileToReadIndex++;
            }

            for (Thread readerThread : readerThreads) {
                try { readerThread.join(); }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for(AsynchronizedFileReader reader : readers) {
            String rawDocument = reader.getContent();
            allFiles.put(new JSONObject(rawDocument));
        }

        return allFiles;
    }
}
