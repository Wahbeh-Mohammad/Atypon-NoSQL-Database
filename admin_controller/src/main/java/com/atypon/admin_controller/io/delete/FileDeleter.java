package com.atypon.admin_controller.io.delete;

import java.io.File;
import java.io.FileNotFoundException;

public class FileDeleter {
    private final File fileToDelete;

    public FileDeleter(File fileToDelete) {
        this.fileToDelete = fileToDelete;
    }

    public void deleteFile() throws FileNotFoundException {
        if(!fileToDelete.exists())
            throw new FileNotFoundException("File not found { " + fileToDelete.getName() + " }");

        if(fileToDelete.isDirectory()) {
            deleteRecursively(fileToDelete);
        } else {
            fileToDelete.delete();
        }
    }

    // Recursive delete for directories that are not empty.
    private void deleteRecursively(File root) {
        if(root == null)
            return;
        File[] children = root.listFiles();
        // check for sub files/dirs and delete them
        if(children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        // after deleting contents delete directory.
        root.delete();
    }
}
