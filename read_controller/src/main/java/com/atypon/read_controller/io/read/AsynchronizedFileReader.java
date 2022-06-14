package com.atypon.read_controller.io.read;

import java.io.*;

public class AsynchronizedFileReader implements Runnable {
    private String content;
    private final File fileToRead;

    public AsynchronizedFileReader(File fileToRead) throws FileNotFoundException {
        if(!fileToRead.exists())
            throw new FileNotFoundException("File doesn't exist { " + fileToRead.getName() + " }");
        this.fileToRead = fileToRead;
        this.content = null;
    }

    public String getContent() {
        return content;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead))) {
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
