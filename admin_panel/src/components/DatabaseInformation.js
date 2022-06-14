import React, { useState, useEffect } from 'react';
import Cookies from 'universal-cookie';

import { Box, Tabs, Tab, Typography, Button, Paper } from '@mui/material';
import CreateSchema from './CreateSchema';

import "../styles/DatabaseInformation.css";
import Schemas from './Schemas';

const DatabaseInformation = (props) => {
    const cookie = new Cookies();
    const { databaseName } = props;

    // database related
    const [schemas, setSchemas] = useState({});
    const [schemaNames, setSchemaNames] = useState([]);

    const parseSchemas = (rawSchemas) => {
        const schemaNames = [];
        const schemasAsJSONObject = {};

        rawSchemas.forEach((fullSchema) => {
            const { schema, info } = fullSchema;
            schemasAsJSONObject[info.schema_name] = schema;
            schemaNames.push(info.schema_name);
        });

        setSchemaNames(schemaNames);
        setSchemas(schemasAsJSONObject);
    }

    const handleDeleteDatabase = () => {
        const token = cookie.get("token");
        fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/admin/database/delete?databaseName=${databaseName}`,
        {
            method:"DELETE",
            headers: {
                "authorization": token
            }
        })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                window.location.reload();
            } else {
                console.log(data.message);
            }
        })
    }

    // view related
    const [currentView , setCurrentView] = useState("database-info");

    const handleTabChange = (e, viewName) => {
        setCurrentView(viewName);
    }

    // exporting
    const handleExport = () => {
        const databaseSchema = {
            databaseName: databaseName,
            schemas
        };

        const jsonString = `data:text/json;chatset=utf-8,${encodeURIComponent(
            JSON.stringify(databaseSchema, null, 2)
        )}`;

        const link = document.createElement("a");
        link.href = jsonString;
        link.download = "databaseSchema.json";
    
        link.click();
    }


    useEffect(() => {
        setCurrentView("database-info")
        const token = cookie.get("read-token");
        fetch(`${process.env.REACT_APP_READCONTROLLER_URL}/read/${databaseName}/schema/all`, {
            method:"GET",
            headers: {
                "authorization": token
            }
        })
        .then(response => response.json())
        .then((data) => {
            if(data.status === "GOOD") {
                parseSchemas(data.content);
            }
        });
    }, [databaseName]);

    return (
        <Box className="db-information">
            <Box className="view-controls flex-row-gap align-center" sx={{borderBottom:1, borderColor:'divider'}}>
                <Typography color="green" fontWeight={"bold"}> Connected To {databaseName} </Typography>
                <Tabs   value={currentView}
                        onChange={handleTabChange}
                        textColor="primary"
                        indicatorColor='primary'>
                    <Tab value="database-info" label="Database Information" />
                    <Tab value="create-new-schema" label="Create New Schema" />
                    <Tab value="schemas" label="Schemas" />
                </Tabs>
            </Box>
            { currentView === "database-info" &&
                <Box className="flex-col-gap section"> 
                    <Box className="padding section-col" component={Paper}>
                        <Typography variant="h4" color="primary"> Export Database Schema </Typography>
                        <Typography variant="h6"> 
                            Exporting provides a json file that contains the 
                            database name, and the schemas which can be used again to import.
                        </Typography>
                        <Box><Button variant="contained" onClick={handleExport}> Export </Button></Box>
                    </Box> 
                    <Box className="padding section-col" component={Paper}>
                        <Typography variant="h4" color="red"> Danger Zone </Typography>
                        <Typography variant="h6" color="red"> 
                            Deleting a database deletes all its information including schemas, and the documents.
                        </Typography>
                        <Box><Button variant="contained" color="error" onClick={handleDeleteDatabase}> Delete Database </Button></Box>
                    </Box>
                </Box>
            }
            { currentView === "create-new-schema" && <CreateSchema databaseName={databaseName}/>}
            { currentView === "schemas" && <Schemas schemas={schemas} schemaNames={schemaNames} databaseName={databaseName} />}
        </Box>
    );
}
 
export default DatabaseInformation;