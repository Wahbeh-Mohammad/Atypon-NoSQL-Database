package com.atypon.admin_controller.io.update;

import com.atypon.admin_controller.flow.UnableToPerformOperationException;

import java.io.*;
import java.util.UUID;

public class FileUpdater {
    private final File oldFile, newFile;
    private final String newContent;

    public FileUpdater(String pathToFile, String fileName, String extension, String newContent)  {
        this.oldFile = new File(pathToFile + fileName + extension);
        String tempUID = UUID.randomUUID().toString();
        this.newFile = new File(pathToFile + tempUID + extension);
        this.newContent = newContent;
    }

    public void updateFile() throws FileNotFoundException{
        if(!oldFile.exists())
            throw new FileNotFoundException("File doesn't exist, updates only happen on existing files");

        try ( BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFile))) {
            bufferedWriter.write(this.newContent);
            bufferedWriter.flush();
            if(!oldFile.delete())
                throw new UnableToPerformOperationException("Something went wrong updating file");
            newFile.renameTo(oldFile);
        } catch (IOException | UnableToPerformOperationException e) {
            newFile.delete();
            e.printStackTrace();
        }
    }
}
