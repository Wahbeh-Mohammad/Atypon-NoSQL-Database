package com.atypon.admin_controller.controller;

import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.utils.ResponseBuilder;
import com.atypon.admin_controller.operations.UpdateOperations;
import com.atypon.admin_controller.utils.AuthenticationUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/update")
public class UpdateController {
    private final AuthenticationUtils authUtils;
    private final UpdateOperations updateOperations;

    @Autowired
    public UpdateController(AuthenticationUtils authUtils, UpdateOperations updateOperations) {
        this.authUtils = authUtils;
        this.updateOperations = updateOperations;
    }

    @CrossOrigin
    @PutMapping( path="/{database}/{schema}/document", produces = "application/json" ) // ?documentId={}
    public ResponseEntity<String> updateDocument(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                 @PathVariable String schema, @RequestParam int documentId,
                                                 @RequestBody HashMap<String, Object> requestBody) {
        if(requestBody == null || requestBody.isEmpty())
            return ResponseBuilder.badRequest("Invalid request body: updates must not be null.");

        if( authUtils.isAuthorizedAdmin(authToken) ) {
            JSONObject updatedDocument = new JSONObject(requestBody);
            AbstractMessage operationMessage = updateOperations.updateDocument(database, schema, documentId, updatedDocument);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @PutMapping( path = "/{database}/schema", produces = "application/json") // ?schemaName={}
    public ResponseEntity<String> updateSchema(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                               @RequestParam String schemaName, @RequestBody HashMap<String, Object> requestBody) {
        if(requestBody == null || requestBody.isEmpty() || invalidParameter(schemaName))
            return ResponseBuilder.badRequest("Invalid request: schema name and updates must not be null.");

        if(authUtils.isAuthorizedAdmin(authToken)) {
            JSONObject updatedSchema = new JSONObject(requestBody);
            AbstractMessage operationMessage = updateOperations.updateSchema(database, schemaName, updatedSchema);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    private boolean invalidParameter(String parameter) {
        return (parameter == null || parameter.equals(""));
    }
}
