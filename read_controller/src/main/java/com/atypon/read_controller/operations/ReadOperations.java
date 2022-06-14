package com.atypon.read_controller.operations;

import com.atypon.read_controller.cache.Cache;
import com.atypon.read_controller.cache.CacheKeyHash;
import com.atypon.read_controller.filter.Filter;
import com.atypon.read_controller.index.IndexInitiator;
import com.atypon.read_controller.json_io.JSONReader;
import com.atypon.read_controller.models.layer_communication.*;
import com.atypon.read_controller.utils.PathBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ReadOperations {
    private final JSONReader reader;
    private final Cache cache;

    @Autowired
    public ReadOperations(JSONReader reader) {
        this.reader = reader;
        this.cache = Cache.getInstance();
    }

    public AbstractMessage fetchById(String databaseName, String schemaName, int id) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR,"Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist.");

        String cacheKeyHash = CacheKeyHash.documentHash(databaseName, schemaName, id);
        JSONObject cachedDocument = cache.get(cacheKeyHash);
        if(cachedDocument != null)
            return new ContentMessage<>(MessageStatus.GOOD, "Cache hit", cachedDocument);

        String pathToDocument = PathBuilder.buildPathToDocument(databaseName, schemaName, id);
        AbstractMessage readMessage = reader.readDocument(pathToDocument);
        if(readMessage.isGood()) {
            // cache document
            JSONObject object = ((ContentMessage<JSONObject>)readMessage).getContent();
            cache.put(cacheKeyHash, object);
        }
        return readMessage;
    }

    public AbstractMessage fetchAllDocuments(String databaseName, String schemaName) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR,"Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist.");

        String pathToAllDocuments = PathBuilder.buildPathToAllDocuments(databaseName, schemaName);
        return reader.readAllDocuments(pathToAllDocuments);
    }

    public AbstractMessage fetchAllIndexed(String databaseName, String schemaName, String fieldName) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR,"Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist.");

        String cacheKeyHash = CacheKeyHash.indexHash(databaseName, schemaName, fieldName);
        JSONObject cachedIndex = cache.get(cacheKeyHash);
        if( cachedIndex != null )
            return new ContentMessage<>(MessageStatus.GOOD, "Cache hit", cachedIndex);

        AbstractMessage readMessage = reader.readAllDocuments(PathBuilder.buildPathToAllDocuments(databaseName, schemaName));

        if(readMessage.isGood()) {
            JSONArray documents = ((ContentMessage<JSONArray>) readMessage).getContent();
            JSONObject indexedDocuments = IndexInitiator.createIndex(documents, fieldName);
            // cache the index, this might be a bad idea due to memory usage.
            cache.put(cacheKeyHash, indexedDocuments);
            return new ContentMessage<>(MessageStatus.GOOD, "Index created", indexedDocuments);
        }

        return readMessage;
    }

    public AbstractMessage fetchWithFilter(String databaseName, String schemaName, String fieldName, String operation, String compareTo) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR,"Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist.");

        AbstractMessage schemaReadMessage = reader.readSchema(PathBuilder.buildPathToSchema(databaseName, schemaName));
        if( !schemaReadMessage.isGood() )
            return schemaReadMessage;
        AbstractMessage documentsReadMessage = reader.readAllDocuments(PathBuilder.buildPathToAllDocuments(databaseName, schemaName));
        if( !documentsReadMessage.isGood() )
            return documentsReadMessage;

        JSONObject schema = ((ContentMessage<JSONObject>)schemaReadMessage).getContent().getJSONObject("schema");

        if(schema.isNull(fieldName))
            return new Message(MessageStatus.USER_ERROR, "Schema has no field { " + fieldName + " }");

        String fieldType = schema.getString(fieldName);

        JSONArray listOfDocuments = ((ContentMessage<JSONArray>)documentsReadMessage).getContent();
        Filter<?> filter = Filter.createFilter(listOfDocuments, fieldType, fieldName, operation, compareTo);
        if(filter == null)
            return new Message(MessageStatus.USER_ERROR, "Couldn't initiate filter with specified criteria");
        JSONArray filteredDocuments = filter.filter();
        return new ContentMessage<>(MessageStatus.GOOD, "Filtered Data", filteredDocuments);
    }

    public AbstractMessage fetchSchema(String databaseName, String schemaName) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR,"Database doesn't exist");
        if(schemaDoesNotExist(databaseName, schemaName))
            return new Message(MessageStatus.USER_ERROR, "Schema doesn't exist.");

        String cacheKeyHash = CacheKeyHash.schemaHash(databaseName, schemaName);
        JSONObject cachedSchema = cache.get(cacheKeyHash);
        if(cachedSchema != null)
            return new ContentMessage<>(MessageStatus.GOOD, "Cache hit", cachedSchema);

        String pathToSchema = PathBuilder.buildPathToSchema(databaseName, schemaName);
        AbstractMessage readMessage = reader.readSchema(pathToSchema);
        if(readMessage.isGood()) {
            // cache schema
            JSONObject schema = ((ContentMessage<JSONObject>)readMessage).getContent();
            cache.put(cacheKeyHash, schema);
        }
        return readMessage;
    }

    public AbstractMessage fetchAllSchemas(String databaseName) {
        if(databaseDoesNotExist(databaseName))
            return new Message(MessageStatus.USER_ERROR,"Database doesn't exist");

        String pathToAllSchemas = PathBuilder.buildPathToAllSchemas(databaseName);
        return reader.readAllSchemas(pathToAllSchemas);
    }

    // Validations and utility.
    private boolean databaseDoesNotExist(String databaseName) {
        File databaseFile = new File(PathBuilder.buildPathToDatabase(databaseName));
        return !databaseFile.exists();
    }

    private boolean schemaDoesNotExist(String databaseName, String schemaName) {
        File schemaFile = new File(PathBuilder.buildPathToSchema(databaseName, schemaName));
        return !schemaFile.exists();
    }
}