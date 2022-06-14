package com.atypon.admin_controller.utils;

public class PathBuilder {
    private static final String PATH_TO_DATA = "./Data/";

    public static String buildPathToDocument(String databaseName, String schemaName, int id) {
        return PATH_TO_DATA + databaseName + "/" + schemaName + "-docs/" + id + ".json";
    }

    public static String buildPathToAllDocuments(String databaseName, String schemaName) {
        return PATH_TO_DATA + databaseName + "/" + schemaName + "-docs/";
    }

    public static String buildPathToSchema(String databaseName, String schemaName) {
        return PATH_TO_DATA + databaseName + "/schemas/" + schemaName + ".json";
    }

    public static String buildPathToAllSchemas(String databaseName) {
        return PATH_TO_DATA + databaseName + "/schemas/";
    }

    public static String buildPathToDatabase(String databaseName) {
        return PATH_TO_DATA + databaseName;
    }

    public static String buildPathToInfo() {
        return PATH_TO_DATA + "info.json";
    }

    public static String buildPathToRoot() {
        return PATH_TO_DATA;
    }
}
