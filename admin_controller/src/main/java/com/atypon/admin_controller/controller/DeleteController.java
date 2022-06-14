package com.atypon.admin_controller.controller;

import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.utils.ResponseBuilder;
import com.atypon.admin_controller.operations.DeleteOperations;
import com.atypon.admin_controller.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delete")
public class DeleteController {
    private final AuthenticationUtils authenticationUtils;
    private final DeleteOperations deleteOperations;

    @Autowired
    public DeleteController(AuthenticationUtils authenticationUtils, DeleteOperations deleteOperations) {
        this.authenticationUtils = authenticationUtils;
        this.deleteOperations = deleteOperations;
    }

    @CrossOrigin
    @DeleteMapping( path="/{database}/schema", produces = "application/json") // ?schemaName={}
    public ResponseEntity<String> deleteSchema(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                               @RequestParam String schemaName) {
        if(invalidParameter(schemaName))
            return ResponseBuilder.badRequest("Invalid request parameter: Schema name cannot be null or empty.");

        if( authenticationUtils.isAuthorizedAdmin(authToken) ) {
            AbstractMessage operationMessage = deleteOperations.deleteSchema(database, schemaName);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @DeleteMapping( path="/{database}/{schema}/document", produces = "application/json") // ?documentId={}
    public ResponseEntity<String> deleteDocument(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                 @PathVariable String schema, @RequestParam String documentId) {
        if(invalidParameter(documentId))
            return ResponseBuilder.badRequest("Invalid request parameter: Document Id cannot be null or empty.");

        if(!authenticationUtils.isAuthorizedAdmin(authToken))
            return ResponseBuilder.badRequest("Unauthorized access");

        try {
            int id = Integer.parseInt(documentId);
            AbstractMessage operationMessage = deleteOperations.deleteDocument(database, schema, id);
            return ResponseBuilder.fromMessage(operationMessage);
        } catch (NumberFormatException e) {
            return ResponseBuilder.badRequest("Invalid Document Id");
        }
    }

    @CrossOrigin
    @DeleteMapping( path="/{database}/{schema}/document/all", produces = "application/json")
    public ResponseEntity<String> deleteAllDocuments(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                     @PathVariable String schema) {
        if(authenticationUtils.isAuthorizedAdmin(authToken)) {
            AbstractMessage operationMessage = deleteOperations.deleteAllDocuments(database, schema);
            return ResponseBuilder.fromMessage(operationMessage);
        }
        return ResponseBuilder.badRequest("Unauthorized access");
    }

    private boolean invalidParameter(String parameter) {
        return (parameter == null || parameter.equals(""));
    }
}
