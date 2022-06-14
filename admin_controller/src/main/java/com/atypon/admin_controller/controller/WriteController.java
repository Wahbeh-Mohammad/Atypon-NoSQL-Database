package com.atypon.admin_controller.controller;

import com.atypon.admin_controller.models.layer_communication.AbstractMessage;
import com.atypon.admin_controller.utils.ResponseBuilder;
import com.atypon.admin_controller.operations.WriteOperations;
import com.atypon.admin_controller.utils.AuthenticationUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/write")
public class WriteController {
    private final AuthenticationUtils authUtils;
    private final WriteOperations writeOperations;

    @Autowired
    public WriteController(AuthenticationUtils authUtils, WriteOperations writeOperations) {
        this.authUtils = authUtils;
        this.writeOperations = writeOperations;
    }

    @CrossOrigin
    @PostMapping( path="/{database}/schema/new", produces="application/json" )
    public ResponseEntity<String> postNewSchema(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                @RequestBody HashMap<String, Object> schemaDetails) {
        if(schemaDetails == null)
            return ResponseBuilder.badRequest("Invalid Request Body: Body must have both schema name and schema details.");

        if(authUtils.isAuthorizedAdmin(authToken)){
            JSONObject jsonBody = new JSONObject(schemaDetails), schema;
            String schemaName = jsonBody.getString("schemaName");
            schema = jsonBody.getJSONObject("schema");
            AbstractMessage operationMessage = writeOperations.createNewSchema(database, schemaName, schema);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }

    @CrossOrigin
    @PostMapping( path = "/{database}/{schema}/document/new", produces = "application/json")
    public ResponseEntity<String> postNewDocument(@RequestHeader("authorization") String authToken, @PathVariable String database,
                                                  @PathVariable String schema, @RequestBody HashMap<String, Object> requestDocument){
        if(requestDocument == null)
            return ResponseBuilder.badRequest("Invalid request body: document must not be null.");

        if(authUtils.isAuthorizedAdmin(authToken)){
            JSONObject newDocument = new JSONObject(requestDocument);
            AbstractMessage operationMessage = writeOperations.createNewDocument(database, schema, newDocument);
            return ResponseBuilder.fromMessage(operationMessage);
        }

        return ResponseBuilder.badRequest("Unauthorized access");
    }
}
