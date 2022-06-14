package com.atypon.admin_controller.io.directory;

import com.atypon.admin_controller.flow.AlreadyExistsException;

import java.io.File;

public class DirectoryCreator {
    private final File directoryToCreate;

    public DirectoryCreator(File directoryToCreate) throws AlreadyExistsException {
        if(directoryToCreate.exists())
            throw new AlreadyExistsException("Directory already exists");
        this.directoryToCreate = directoryToCreate;
    }

    public void createDirectory() {
        directoryToCreate.mkdir();
    }
}
