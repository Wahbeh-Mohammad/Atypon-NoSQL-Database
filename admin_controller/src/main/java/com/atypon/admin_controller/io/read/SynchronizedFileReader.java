package com.atypon.admin_controller.io.read;

import java.io.*;

public class SynchronizedFileReader {
    private final File fileToRead;
    private String content;

    public SynchronizedFileReader(File fileToRead) throws FileNotFoundException {
        if(!fileToRead.exists())
            throw new FileNotFoundException("File doesn't exist { " + fileToRead.getName() + " }");
        this.fileToRead = fileToRead;
        this.content = null;
    }

    public String getContent() {
        return content;
    }

    public void readFile() {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead))) {
            StringBuilder stringBuilder = new StringBuilder();
            String lineHolder;
            while ((lineHolder = bufferedReader.readLine()) != null)
                stringBuilder.append(lineHolder);
            this.content = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
