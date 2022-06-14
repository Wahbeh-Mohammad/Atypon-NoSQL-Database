package com.atypon.read_controller.operations;

import com.atypon.read_controller.io.read.SynchronizedFileReader;
import com.atypon.read_controller.models.types.User;
import com.atypon.read_controller.models.layer_communication.AbstractMessage;
import com.atypon.read_controller.models.layer_communication.ContentMessage;
import com.atypon.read_controller.models.layer_communication.Message;
import com.atypon.read_controller.models.layer_communication.MessageStatus;
import com.atypon.read_controller.utils.PathBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class AuthenticationOperations {
    public AbstractMessage fetchUser(User user) {
        try {
            SynchronizedFileReader reader = new SynchronizedFileReader(new File(PathBuilder.buildPathToInfo()));
            reader.readFile();
            JSONObject allUsers = new JSONObject(reader.getContent()).getJSONObject("users"), specificUser;
            String identifier = String.valueOf(user.hashCode());
            try {
                // check if there is a user with the hash value.
                specificUser = allUsers.getJSONObject(identifier);
            } catch (JSONException e) {
                return new Message(MessageStatus.USER_ERROR, "No user found with the specified credentials");
            }
            return new ContentMessage<>(MessageStatus.GOOD, "User authorized", specificUser);
        } catch (FileNotFoundException e) {
            return new Message(MessageStatus.BAD, "Database is not setup correctly.");
        }
    }
}
