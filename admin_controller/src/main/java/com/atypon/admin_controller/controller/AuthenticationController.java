package com.atypon.admin_controller.controller;

import com.atypon.admin_controller.authentication.TokenAuthentication;
import com.atypon.admin_controller.models.types.User;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.models.layer_communication.ContentMessage;
import com.atypon.admin_controller.models.layer_communication.Message;
import com.atypon.admin_controller.models.layer_communication.MessageStatus;
import com.atypon.admin_controller.utils.AuthenticationUtils;
import com.atypon.admin_controller.utils.ResponseBuilder;
import com.atypon.admin_controller.operations.AuthenticationOperations;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final TokenAuthentication authentication;
    private final AuthenticationOperations authOperations;
    private final AuthenticationUtils authUtils;

    @Autowired
    public AuthenticationController(TokenAuthentication authentication, AuthenticationOperations authOperations, AuthenticationUtils authUtils) {
        this.authentication = authentication;
        this.authOperations = authOperations;
        this.authUtils = authUtils;
    }

    @CrossOrigin
    @PostMapping(path="/login", produces="application/json")
    public ResponseEntity<String> login(@RequestBody User user) {
        if(user.getUsername() == null || user.getPassword() == null)
            return ResponseBuilder.badRequest("Username and password cannot be null.");

        AbstractMessage operationMessage = authOperations.fetchUser(user);

        if(operationMessage.isGood()) {
            ContentMessage<JSONObject> messageWithUser = ((ContentMessage<JSONObject>) operationMessage);
            JSONObject userFromDb = messageWithUser.getContent();
            String token = authentication.sign(userFromDb);
            return ResponseBuilder.fromMessage(new ContentMessage<>(MessageStatus.GOOD, "Authorized", token));
        }

        return ResponseBuilder.fromMessage(operationMessage);
    }

    @CrossOrigin
    @GetMapping(path= "/user/verifyAdmin", produces="application/json")
    public ResponseEntity<String> verifyAdmin(@RequestHeader("authorization") String authToken) {
        if(authUtils.isAuthorizedAdmin(authToken))
            return ResponseBuilder.fromMessage(new Message(MessageStatus.GOOD, "Verified"));
        return ResponseBuilder.badRequest("Invalid/Expired Token, Unauthorized access");
    }
}
