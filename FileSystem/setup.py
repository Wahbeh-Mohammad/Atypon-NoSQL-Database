import os
import json
import shutil

CURRENT_DIR = os.getcwd()
ROOT_DIR_NAME = "Data"
DEFAULT_INFO_FILE = {
    "databases": [],
    "users": {
        "817916384": { # this number is the hashCode of "username" & "password" of values admin & admin
            "username": "admin",
            "password": "admin",
            "role": "ADMIN"
        }
    }
}

def writeInfoFile():
    pathToInfoFile = CURRENT_DIR + "/" + ROOT_DIR_NAME + "/info.json"
    with open(pathToInfoFile, "w") as File:
        File.write(json.dumps(DEFAULT_INFO_FILE, separators=(',', ':')))
    

def createDirectory(pathToDirectory):
    try:
        os.mkdir(pathToDirectory)
    except Exception as exception:
        print("Couldn't create directory { "+ pathToDirectory + " } Exception:")
        print(exception)
        print("\nExiting script.")
        exit(1)

def deleteDirectory(pathToDirectory):
    try:
        shutil.rmtree(pathToDirectory) # removes tree recursively
    except Exception as exception:
        print("Couldn't delete directory { "+ pathToDirectory + " } Exception:")
        print(exception)
        print("\nExiting script.")
        exit(1)

def setupDatabase():
    fullPathToData = CURRENT_DIR + "/" + ROOT_DIR_NAME + "/"
    listOfFiles = os.listdir(CURRENT_DIR)

    if "Data" in listOfFiles:
        print("A Directory named Data exists in the current directory")
        confirmation = input("Do you want to override it? (Y/N) ").lower()
        if confirmation == "y" or confirmation == "":
            deleteDirectory(fullPathToData)
            createDirectory(fullPathToData)
            writeInfoFile()
        else:
            print("Initialization cancelled, exiting script.")
            exit(0)
    else:
        print("Database file structure will be created in directory: { " + CURRENT_DIR + " }")
        print("Initializing file structure in root directory: { " + ROOT_DIR_NAME + " }")
        createDirectory(fullPathToData)
        writeInfoFile()

if __name__ == "__main__":
    setupDatabase()