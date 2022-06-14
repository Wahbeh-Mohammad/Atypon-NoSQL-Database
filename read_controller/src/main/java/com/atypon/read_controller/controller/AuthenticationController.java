package com.atypon.read_controller.controller;

import com.atypon.read_controller.authentication.TokenAuthentication;
import com.atypon.read_controller.models.types.User;
import com.atypon.read_controller.models.layer_communication.AbstractMessage;
import com.atypon.read_controller.models.layer_communication.ContentMessage;
import com.atypon.read_controller.models.layer_communication.MessageStatus;
import com.atypon.read_controller.utils.ResponseBuilder;
import com.atypon.read_controller.operations.AuthenticationOperations;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    TokenAuthentication authentication;
    AuthenticationOperations authOperations;

    @Autowired
    public AuthenticationController(TokenAuthentication authentication, AuthenticationOperations authOperations) {
        this.authentication = authentication;
        this.authOperations = authOperations;
    }

    @CrossOrigin
    @PostMapping(path = "/login", produces = "application/json")
    public ResponseEntity<String> login(@RequestBody User user) {
        System.out.println("Login request on [" + System.getenv("HOSTNAME") + "]");
        if (user.getUsername() == null || user.getPassword() == null)
            return ResponseBuilder.badRequest("Username and password cannot be null.");

        AbstractMessage authMessage = authOperations.fetchUser(user);

        if (authMessage.isGood()) {
            JSONObject userCredentials = ((ContentMessage<JSONObject>) authMessage).getContent();
            String token = authentication.sign(userCredentials);
            return ResponseBuilder.fromMessage(new ContentMessage<>(MessageStatus.GOOD, "Authorized", token));
        }

        return ResponseBuilder.fromMessage(authMessage);
    }
}
