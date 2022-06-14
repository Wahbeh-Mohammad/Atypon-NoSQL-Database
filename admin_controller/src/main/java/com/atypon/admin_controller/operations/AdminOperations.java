package com.atypon.admin_controller.operations;

import com.atypon.admin_controller.flow.AlreadyExistsException;
import com.atypon.admin_controller.io.delete.FileDeleter;
import com.atypon.admin_controller.io.directory.DirectoryCreator;
import com.atypon.admin_controller.io.read.SynchronizedFileReader;
import com.atypon.admin_controller.io.update.FileUpdater;
import com.atypon.admin_controller.models.types.User;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.ContentMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import com.atypon.admin_controller.utils.PathBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class AdminOperations {
    public AbstractMessage createDatabase(String databaseName) {
        try {
            String pathToNewDatabase = PathBuilder.buildPathToDatabase(databaseName),
                    pathToSchemasRoot = PathBuilder.buildPathToAllSchemas(databaseName);
            synchronized (this) {
                DirectoryCreator databaseDirectory = new DirectoryCreator(new File(pathToNewDatabase));
                databaseDirectory.createDirectory();
                DirectoryCreator schemasDirectory = new DirectoryCreator(new File(pathToSchemasRoot));
                schemasDirectory.createDirectory();

                // add the new database name to the info.json -> databases prop
                JSONObject infoJSON = fetchDatabaseInformation();
                JSONArray listOfDatabases = infoJSON.getJSONArray("databases");
                listOfDatabases.put(databaseName);
                infoJSON.put("databases", listOfDatabases);

                updateDatabaseInformation(infoJSON);

                return new Message(MessageStatus.GOOD, "New database created");
            }
        } catch (AlreadyExistsException e) {
            return new Message(MessageStatus.USER_ERROR, "Database already exists");
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.BAD, "File System is not setup correctly.");
        }
    }

    public AbstractMessage deleteDatabase(String databaseName){
        String pathToDatabase = PathBuilder.buildPathToDatabase(databaseName);
        try {
            synchronized (this) {
                FileDeleter fileDeleter = new FileDeleter(new File(pathToDatabase));
                fileDeleter.deleteFile();
                JSONObject infoObject = fetchDatabaseInformation();
                JSONArray databases = infoObject.getJSONArray("databases");
                for (int databaseIndex = 0; databaseIndex < databases.length(); databaseIndex++) {
                    String currentDatabaseName = databases.getString(databaseIndex);
                    if (currentDatabaseName.equals(databaseName)) {
                        databases.remove(databaseIndex);
                        break;
                    }
                }
                infoObject.put("databases", databases);
                updateDatabaseInformation(infoObject);
                return new Message(MessageStatus.GOOD, "Database deleted");
            }
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.USER_ERROR, "Database doesn't exist");
        }
    }

    public AbstractMessage createUser(User newUser) {
        try {
            if( !newUser.isValidUser() )
                throw new IllegalArgumentException("New user's name, password and role cannot be null");

            synchronized (this) {
                JSONObject infoJSON = fetchDatabaseInformation(),
                           allUsers = infoJSON.getJSONObject("users");

                String userHash = String.valueOf(newUser.hashCode());
                if ( !allUsers.isNull(userHash) )
                    throw new AlreadyExistsException("User already exists.");

                allUsers.put(userHash, newUser.toJSON());
                infoJSON.put("users", allUsers);

                updateDatabaseInformation(infoJSON);

                return new Message(MessageStatus.GOOD, "New user created");
            }
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.BAD, "File System is not setup correctly.");
        } catch (IllegalArgumentException | AlreadyExistsException e) {
            return new Message(MessageStatus.USER_ERROR, e.getMessage());
        }
    }

    public AbstractMessage updateUser(User oldData, User newData) {
        try {
            synchronized (this) {
                JSONObject infoJSON = fetchDatabaseInformation(),
                           allUsers = infoJSON.getJSONObject("users");

                newData.setRole(oldData.getRole()); // override new role with old role.

                String oldKey = String.valueOf(oldData.hashCode()),
                       newKey = String.valueOf(newData.hashCode());

                if(allUsers.isNull(oldKey))
                    return new Message(MessageStatus.USER_ERROR, "User doesn't exist");
                allUsers.remove(oldKey);

                allUsers.put(newKey, newData.toJSON());
                infoJSON.put("users", allUsers);
                updateDatabaseInformation(infoJSON);

                return new Message(MessageStatus.GOOD, "User updated");
            }
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.BAD, "File System is not setup correctly");
        }
    }

    public AbstractMessage deleteUser(User userToDelete) {
        try {
            synchronized (this) {
                JSONObject infoJSON = fetchDatabaseInformation(),
                           allUsers = infoJSON.getJSONObject("users");
                String key = String.valueOf(userToDelete.hashCode());
                allUsers.remove(key);
                infoJSON.put("users", allUsers);
                updateDatabaseInformation(infoJSON);

                return new Message(MessageStatus.GOOD, "User deleted successfully");
            }
        } catch (JSONException e) {
            return new Message(MessageStatus.USER_ERROR, "User not found");
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.BAD, "File System is not setup correctly");
        }
    }

    public AbstractMessage readAllUsers() {
        try {
            JSONObject allUsers = fetchDatabaseInformation().getJSONObject("users");
            return new ContentMessage<>(MessageStatus.GOOD, "Read successful", allUsers);
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.BAD, "File System is not setup correctly.");
        }
    }

    public AbstractMessage fetchAllDatabaseNames() {
        try {
            JSONArray listOfDatabases = fetchDatabaseInformation().getJSONArray("databases");
            return new ContentMessage<>(MessageStatus.GOOD, "Read successful", listOfDatabases);
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.BAD, "File System is not setup correctly.");
        }
    }

    private JSONObject fetchDatabaseInformation() throws FileNotFoundException {
        SynchronizedFileReader reader = new SynchronizedFileReader(new File(PathBuilder.buildPathToInfo()));
        reader.readFile();
        return new JSONObject(reader.getContent());
    }

    private synchronized void updateDatabaseInformation(JSONObject updates) throws FileNotFoundException {
        FileUpdater fileUpdater = new FileUpdater(PathBuilder.buildPathToRoot(), "info", ".json", updates.toString(2));
        fileUpdater.updateFile();
    }
}
