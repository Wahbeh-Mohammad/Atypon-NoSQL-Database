import React, { useEffect, useState } from "react";
import Cookies from "universal-cookie";
import { Typography, TextField, Button, Paper } from "@mui/material";
import { validateSchema } from "../utils/validation";
import "../styles/CreateDatabase.css";

const CreateDatabase = (props) => {
    const cookie = new Cookies();
    // creation from name
    const [databaseName, setDatabaseName] = useState("");
    const [createInfo, setCreateInfo] = useState("");
    // creation from import
    const [importInfo, setImportInfo] = useState("");
    const [importInfoColor, setImportInfoColor] = useState("");
    const [importedDatabaseName, setImportedDatabaseName] = useState("");
    const [importedSchemas, setImportedSchemas] = useState([]);

    const handleCreateDatabase = async () => {
        setCreateInfo("");
        if(databaseName == null || databaseName === "") {
            setCreateInfo("Database name cannot be null or empty");
            return;
        }
        
        const token = cookie.get("token");
        const creationResponse = await fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/admin/database/new?databaseName=${databaseName}`, {
            method:"POST",
            headers: {
                "authorization": token
            }
        });

        const jsonResponse = await creationResponse.json();
        if(jsonResponse.status === "GOOD") {
            window.location.reload()
        } else {
            setCreateInfo(jsonResponse.message);
        }
    }

    const handleSubmitImport = async () => {
        const token = cookie.get("token");
        const creationResponse = await fetch(
            `${process.env.REACT_APP_ADMINCONTROLLER_URL}/admin/database/new?databaseName=${importedDatabaseName}`, 
            {
                method:"POST",
                headers: {
                    "authorization": token
                }
            }
        );

        const jsonResponse = await creationResponse.json();
        if(jsonResponse.status === "GOOD") {
            importedSchemas.forEach( (schema) => {
                fetch(
                    `${process.env.REACT_APP_ADMINCONTROLLER_URL}/write/${importedDatabaseName}/schema/new`, {
                    method:"POST",
                    headers: {
                        "content-type":"application/json",
                        "authorization":token
                    },
                    body: schema
                })
                .then(response => response.json())
                .then(data => console.log(data));
            });
            window.location.reload();
        } else {
            setImportInfo(jsonResponse.message);
            setImportInfoColor("red");
        }
    }

    const handleImport = (e) => {
        setImportInfo(``);
        setImportInfoColor("");
        setImportedDatabaseName("");
        setImportedSchemas([]);
        try {
            const fileReader = new FileReader();
            fileReader.readAsText(e.target.files[0], "UTF-8");
            fileReader.onload = e => {
                const { databaseName, schemas } = JSON.parse(e.target.result);
                if(databaseName === null || databaseName === "") {
                    setImportInfo("database name cannot be null.");
                    setImportInfoColor("red");
                    return;
                }

                if(schemas === null || schemas === {} || schemas === undefined){
                    setImportInfo("database name cannot be null.");
                    setImportInfoColor("red");
                    return;
                }

                const schemasArr = [];
                const schemaNames = Object.keys(schemas);
                for(var schemaIndex = 0; schemaIndex < schemaNames.length; schemaIndex++) {
                    var schemaName = schemaNames[schemaIndex];
                    var schema = schemas[schemaName];
                    console.log(schema);
                    var _fullSchema = JSON.stringify({schemaName, schema});
                    const validation = validateSchema(_fullSchema);
                    schemasArr.push(_fullSchema);
                    if(!validation.valid){
                        setImportInfo(`Schema is invalid: ${schemaName}`);
                        setImportInfoColor("red");
                        return;
                    }
                }

                setImportInfo(`Database schema is valid`);
                setImportInfoColor("green");
                setImportedDatabaseName(databaseName);
                setImportedSchemas(schemasArr);
            }
        } catch (e) {
            setImportInfo("Invalid database schema");
            setImportInfoColor("red");
        }
    }

    useEffect(()=>{
        const token = cookie.get("token");
        if(!token) {
            window.location.assign("/"); 
        } else {
            fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/auth/user/verifyAdmin`, {
                method:"GET",
                headers: {
                    "authorization": token
                }
            })
            .then((response) => response.json())
            .then((data) => {
                if(data.status !== "GOOD") {
                    cookie.remove("token");
                    window.location.assign("/");
                }
            });
        }
    }, [])

    return (
        <Paper elevation={6} className="create-database flex-col-gap">
            <Typography color="primary" variant="h5"> Create a new database </Typography>
            <TextField 
                autoComplete="off"
                size="large" 
                label="Database Name" 
                value={ databaseName || "" }
                onChange={e => setDatabaseName(e.target.value)}
            />
            <Button variant="contained" onClick={handleCreateDatabase}> Create Database </Button>
            { createInfo && <Typography variant="h6" color="red"> {createInfo} </Typography>  }
            <Typography color="primary" variant="h5"> Or import a database schema </Typography>
            <Button variant="outlined" component="label"> Upload Database Schema <input type="file" onChange={handleImport} hidden/></Button>
            { importInfo && <Typography variant="h6" color={importInfoColor}> { importInfo } </Typography> }
            <Button variant="contained" onClick={handleSubmitImport}> Create database from schema </Button>
        </Paper> 
    );
}
 
export default CreateDatabase;