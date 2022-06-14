package com.atypon.read_controller.controller;

import com.atypon.read_controller.cache.Cache;
import com.atypon.read_controller.models.layer_communication.AbstractMessage;
import com.atypon.read_controller.models.layer_communication.Message;
import com.atypon.read_controller.models.layer_communication.MessageStatus;
import com.atypon.read_controller.utils.ResponseBuilder;
import com.atypon.read_controller.operations.ReadOperations;
import com.atypon.read_controller.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/read")
public class ReadController {
    private final AuthenticationUtils authentication;
    private final ReadOperations readOperations;
    private final Cache cache;

    @Autowired
    public ReadController(AuthenticationUtils authentication, ReadOperations readOperations) {
        this.authentication = authentication;
        this.readOperations = readOperations;
        this.cache = Cache.getInstance();
    }

    @CrossOrigin
    @GetMapping(path="/cache/refresh", produces="application/json")
    public ResponseEntity<String> refreshCache(@RequestHeader("authorization") String authToken) {
        if( authentication.isAuthorizedAdmin(authToken) ) {
            cache.refreshCache();
            return ResponseBuilder.fromMessage(new Message(MessageStatus.GOOD, "Cache refreshed"));
        }
        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @GetMapping(path="/{database}/{schema}/document", produces="application/json") // ?documentId={}
    public ResponseEntity<String> getSpecificDocument(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                      @PathVariable String schema, @RequestParam int documentId) {
        // in read operations we just have to check if the user has a valid token, role doesn't matter.
        if(authentication.isAuthorized(authToken)){
            AbstractMessage operationMessage = readOperations.fetchById(database, schema, documentId);
            return ResponseBuilder.fromMessage(operationMessage);
        }
        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @GetMapping(path="/{database}/{schema}/document/all", produces="application/json")
    public ResponseEntity<String> getAllDocuments(@PathVariable String database, @PathVariable String schema,
                                                  @RequestHeader("authorization") String authToken) {
        if(authentication.isAuthorized(authToken)) {
            AbstractMessage operationMessage = readOperations.fetchAllDocuments(database, schema);
            return ResponseBuilder.fromMessage(operationMessage);
        }
        return ResponseBuilder.badRequest("Unauthorized access");
    }

    // Filter parameters:
    // fieldName: Field to filter by.
    // op: Operation to filter by, { equals, notEquals }.
    // compareTo: the value to filter by.
    @CrossOrigin
    @GetMapping( path ="/{database}/{schema}/filter", produces = "application/json") // ?fieldName={}&op={}&compareTo={}
    public ResponseEntity<String> getFilteredDocuments(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                       @PathVariable String schema, @RequestParam String fieldName,
                                                       @RequestParam String op, @RequestParam String compareTo) {
        if(invalidParameter(fieldName) || invalidParameter(op) || invalidParameter(compareTo))
            return ResponseBuilder.badRequest("Field name, operation and compareTo cannot be null.");

        if(authentication.isAuthorized(authToken)) {
            AbstractMessage operationMessage = readOperations.fetchWithFilter(database, schema, fieldName, op, compareTo);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @GetMapping( path="/{database}/{schema}/indexed", produces="application/json") // ?fieldName={}
    public ResponseEntity<String> getAllDocumentsIndexed(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                         @PathVariable String schema, @RequestParam String fieldName) {
        if(fieldName == null)
            return ResponseBuilder.badRequest("Field name to index by cannot be null");

        if(authentication.isAuthorized(authToken)) {
            AbstractMessage operationMessage = readOperations.fetchAllIndexed(database, schema, fieldName);
            return ResponseBuilder.fromMessage(operationMessage);
        }
        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @GetMapping(path="/{database}/schema", produces="application/json") // ?schemaName={}
    public ResponseEntity<String> getSpecificSchema(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                    @RequestParam String schemaName) {
        if(invalidParameter(schemaName))
            return ResponseBuilder.badRequest("Schema name cannot be null.");

        if(authentication.isAuthorized(authToken)) {
            AbstractMessage operationMessage = readOperations.fetchSchema(database, schemaName);
            return ResponseBuilder.fromMessage(operationMessage);
        }
        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @GetMapping(path="/{database}/schema/all", produces="application/json")
    public ResponseEntity<String> getAllSchemas(@RequestHeader("authorization") String authToken, @PathVariable String database) {
        if(authentication.isAuthorized(authToken)) {
            AbstractMessage operationMessage = readOperations.fetchAllSchemas(database);
            return ResponseBuilder.fromMessage(operationMessage);
        }
        return ResponseBuilder.badRequest("Unauthorized access");
    }

    private boolean invalidParameter(String parameter) {
        return (parameter == null || parameter.equals(""));
    }
}
