package com.atypon.read_controller.cache;

public class CacheKeyHash {

    public static String schemaHash(String databaseName, String schemaName) {
        return databaseName + "-" + schemaName;
    }

    public static String documentHash(String databaseName, String schemaName, int id) {
        return databaseName + "-" + schemaName + "-" + id;
    }

    public static String indexHash(String databaseName, String schemaName, String fieldName) {
        return databaseName + "-" + schemaName + "index/" + fieldName;
    }
}