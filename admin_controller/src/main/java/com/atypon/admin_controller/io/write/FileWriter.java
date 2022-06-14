package com.atypon.admin_controller.io.write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class FileWriter {
    private final File fileToWrite;
    private final String content;

    public FileWriter(File fileToWrite, String content){
        this.fileToWrite = fileToWrite;
        this.content = content;
    }

    public void writeFile() throws FileAlreadyExistsException {
        if(fileToWrite.exists())
            throw new FileAlreadyExistsException("Only updates can be used on already existing files.");
        if(content == null)
            throw new IllegalArgumentException("Content to write cannot be null.");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new java.io.FileWriter(fileToWrite))) {
            bufferedWriter.write(content);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
