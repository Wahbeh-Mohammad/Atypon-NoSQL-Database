package com.atypon.admin_controller.operations;

import com.atypon.admin_controller.io.read.SynchronizedFileReader;
import com.atypon.admin_controller.models.types.User;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.ContentMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import com.atypon.admin_controller.utils.PathBuilder;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class AuthenticationOperations {
    public AbstractMessage fetchUser(User user) {
        try {
            JSONObject allUsers = fetchDatabaseInformation().getJSONObject("users");
            String identifier = String.valueOf(user.hashCode());
            if(allUsers.isNull(identifier)) {
                return new Message(MessageStatus.USER_ERROR, "User not found.");
            }
            JSONObject specificUser = allUsers.getJSONObject(identifier);
            return new ContentMessage<>(MessageStatus.GOOD, "User found", specificUser);
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.BAD, "File System is not setup correctly.");
        }
    }

    private JSONObject fetchDatabaseInformation() throws FileNotFoundException {
        SynchronizedFileReader reader = new SynchronizedFileReader(new File(PathBuilder.buildPathToInfo()));
        reader.readFile();
        return new JSONObject(reader.getContent());
    }
}
