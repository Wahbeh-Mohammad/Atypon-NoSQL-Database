package com.atypon.admin_controller.controller;

import com.atypon.admin_controller.authentication.TokenAuthentication;
import com.atypon.admin_controller.models.types.User;
import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.utils.ResponseBuilder;
import com.atypon.admin_controller.operations.AdminOperations;
import com.atypon.admin_controller.utils.AuthenticationUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AuthenticationUtils authenticationUtils;
    private final AdminOperations adminOperations;
    private final TokenAuthentication tokenAuthentication;

    @Autowired
    public AdminController(AuthenticationUtils authenticationUtils, AdminOperations adminOperations, TokenAuthentication tokenAuthentication) {
        this.authenticationUtils = authenticationUtils;
        this.adminOperations = adminOperations;
        this.tokenAuthentication = tokenAuthentication;
    }

    @CrossOrigin
    @PostMapping( path="/database/new", produces = "application/json") // ?databaseName={}
    public ResponseEntity<String> postNewDatabase(@RequestHeader("authorization") String authToken,@RequestParam String databaseName) {
        if (invalidParameter(databaseName))
            return ResponseBuilder.badRequest("Database name cannot be null or empty.");

        if ( authenticationUtils.isAuthorizedAdmin(authToken) ) {
            AbstractMessage operationMessage = adminOperations.createDatabase(databaseName);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @DeleteMapping( path = "/database/delete", produces = "application/json") // ?databaseName={}
    public ResponseEntity<String> deleteDatabase(@RequestHeader("authorization") String authToken, @RequestParam String databaseName) {
        if (invalidParameter(databaseName))
            return ResponseBuilder.badRequest("Invalid request parameter: Database name cannot be null or empty.");

        if (authenticationUtils.isAuthorizedAdmin(authToken)) {
            AbstractMessage operationMessage = adminOperations.deleteDatabase(databaseName);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @PostMapping( path = "/user/new", produces = "application/json")
    public ResponseEntity<String> postNewUser(@RequestHeader("authorization") String authToken,@RequestBody User requestUser) {
        if(requestUser == null)
            return ResponseBuilder.badRequest("Invalid Request Body: User credentials are null.");

        if( authenticationUtils.isAuthorizedAdmin(authToken) ){
            AbstractMessage operationMessage = adminOperations.createUser(requestUser);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @PutMapping(path="/user/update", produces = "application/json")
    public ResponseEntity<String> updateUser(@RequestHeader("authorization") String authToken,@RequestBody User updatedUser) {
        if(invalidParameter(updatedUser.getUsername()) || invalidParameter(updatedUser.getPassword()))
            return ResponseBuilder.badRequest("Invalid Request Body: username or password cannot be empty or null.");

        JSONObject userCredentials = tokenAuthentication.verify(authToken);
        if(userCredentials == null)
            return ResponseBuilder.badRequest("Unauthorized access");
        User authorizedUser = User.fromJSON(userCredentials);

        AbstractMessage operationMessage = adminOperations.updateUser(authorizedUser, updatedUser);
        return ResponseBuilder.fromMessage(operationMessage);
    }

    @CrossOrigin
    @DeleteMapping(path="/user/delete", produces="application/json")
    public ResponseEntity<String> deleteUser(@RequestHeader("authorization") String authToken, @RequestBody User userToDelete) {
        if(!userToDelete.isValidUser())
            return ResponseBuilder.badRequest("Invalid request body: username, password and role cannot be null.");

        if(authenticationUtils.isAuthorizedAdmin(authToken) ) {
            AbstractMessage operationMessage = adminOperations.deleteUser(userToDelete);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @GetMapping( path="/user/all", produces = "application/json" )
    public ResponseEntity<String> getAllUsers(@RequestHeader("authorization") String authToken) {
        if(!authenticationUtils.isAuthorizedAdmin(authToken))
            return ResponseBuilder.badRequest("Unauthorized access");

        AbstractMessage operationMessage = adminOperations.readAllUsers();
        return ResponseBuilder.fromMessage(operationMessage);
    }

    @CrossOrigin
    @GetMapping( path="/database/all", produces = "application/json")
    public ResponseEntity<String> getAllDatabaseNames(@RequestHeader("authorization") String authToken) {
        if(!authenticationUtils.isAuthorizedAdmin(authToken))
            return ResponseBuilder.badRequest("Unauthorized access");

        AbstractMessage operationMessage = adminOperations.fetchAllDatabaseNames();
        return ResponseBuilder.fromMessage(operationMessage);
    }

    private boolean invalidParameter(String parameter) {
        return (parameter == null || parameter.equals(""));
    }
}
